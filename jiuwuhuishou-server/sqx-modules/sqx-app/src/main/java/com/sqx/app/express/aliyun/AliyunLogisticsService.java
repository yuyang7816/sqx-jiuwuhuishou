package com.sqx.app.express.aliyun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sqx.app.base.constants.SysConfigConstant;
import com.sqx.app.order.domain.vo.ExpressTrackVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.http.OkHttpUtil;
import com.sqx.common.redis.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 阿里云市场·全国快递物流查询（APPCODE 鉴权）
 * <p>
 * 文档地址：https://wuliu.market.alicloudapi.com/kdi
 * <p>
 * 顺丰单号需附带收/寄件人手机尾号 4 位，格式：SF12345678:0123
 */
@Slf4j
@Service
public class AliyunLogisticsService {

    private static final String HOST = "https://wuliu.market.alicloudapi.com";
    private static final String PATH = "/kdi";
    private static final Pattern COURIER_PHONE_PREFIX = Pattern.compile("^(.*?):");

    public boolean isConfigured() {
        return StringUtils.isNotBlank(resolveAppCode());
    }

    /**
     * 按运单号查询物流轨迹
     */
    public List<ExpressTrackVo> listExpressTracks(String trackingNumber) {
        return listExpressTracks(trackingNumber, (String[]) null);
    }

    /**
     * @param phones 收/寄件人手机号（优先用联系人，其次平台收件电话）；顺丰会取尾号 4 位拼到单号后
     */
    public List<ExpressTrackVo> listExpressTracks(String trackingNumber, String... phones) {
        List<String> candidates = buildQueryCandidates(trackingNumber, phones);
        if (candidates.isEmpty()) {
            return new ArrayList<>();
        }

        String appCode = resolveAppCode();
        if (StringUtils.isBlank(appCode)) {
            throw new BizException("物流查询未配置，请在系统配置填写 aliyun_logistics_appcode 并刷新缓存");
        }

        BizException lastError = null;
        for (int i = 0; i < candidates.size(); i++) {
            String queryNo = candidates.get(i);
            try {
                return doQuery(queryNo, appCode);
            } catch (BizException ex) {
                lastError = ex;
                boolean canRetry = i < candidates.size() - 1 && isPhoneTailRequired(ex.getMessage());
                if (!canRetry) {
                    throw ex;
                }
                log.info("物流查询换手机尾号重试: from={}, next={}", queryNo, candidates.get(i + 1));
            }
        }
        throw lastError != null ? lastError : new BizException("查询物流轨迹失败");
    }

    /**
     * 组装查询单号候选：顺丰优先「单号:尾号」；已带冒号后缀则原样查询
     */
    List<String> buildQueryCandidates(String trackingNumber, String... phones) {
        String raw = StringUtils.trimToEmpty(trackingNumber);
        if (StringUtils.isBlank(raw)) {
            return new ArrayList<>();
        }
        // 已含尾号：SF123:0123
        if (raw.contains(":") && raw.indexOf(':') < raw.length() - 1) {
            List<String> single = new ArrayList<>();
            single.add(raw);
            return single;
        }
        String base = StringUtils.removeEnd(raw, ":");
        List<String> last4List = collectLast4(phones);
        boolean sf = isSfExpress(base);

        List<String> candidates = new ArrayList<>();
        if (sf) {
            if (last4List.isEmpty()) {
                throw new BizException("顺丰单号查询需提供收/寄件人手机号后四位，请检查订单联系电话或平台收件电话配置");
            }
            for (String last4 : last4List) {
                candidates.add(base + ":" + last4);
            }
            return candidates;
        }
        candidates.add(base);
        for (String last4 : last4List) {
            candidates.add(base + ":" + last4);
        }
        return candidates;
    }

    private List<ExpressTrackVo> doQuery(String queryNo, String appCode) {
        Map<String, String> params = new HashMap<>();
        params.put("no", queryNo);
        params.put("type", "");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appCode);

        String json;
        try {
            json = OkHttpUtil.doGet(HOST + PATH, params, headers);
        } catch (Exception ex) {
            log.warn("阿里云物流查询请求失败: no={}, err={}", queryNo, ex.getMessage());
            throw new BizException("查询物流轨迹失败：" + StringUtils.defaultIfBlank(ex.getMessage(), "请稍后重试"));
        }
        if (StringUtils.isBlank(json)) {
            throw new BizException("查询物流轨迹失败：无返回内容");
        }
        return parseTracks(json);
    }

    /**
     * 解析阿里云物流响应为轨迹列表（包内可见，便于单测）
     */
    List<ExpressTrackVo> parseTracks(String json) {
        JSONObject root = JSON.parseObject(json);
        if (root == null) {
            throw new BizException("查询物流轨迹失败：返回格式错误");
        }
        String statusCode = root.getString("status");
        if (statusCode != null && !"0".equals(statusCode)) {
            String msg = StringUtils.defaultIfBlank(root.getString("msg"), "物流单号有误请联系发货人");
            throw new BizException(msg);
        }

        JSONObject result = root.getJSONObject("result");
        if (result == null) {
            return new ArrayList<>();
        }
        normalizeCourierPhone(result);

        JSONArray list = result.getJSONArray("list");
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        List<ExpressTrackVo> tracks = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            JSONObject node = list.getJSONObject(i);
            if (node == null) {
                continue;
            }
            String statusText = StringUtils.defaultIfBlank(node.getString("status"), "物流更新");
            ExpressTrackVo vo = new ExpressTrackVo();
            vo.setTitle(truncateTitle(statusText));
            vo.setTime(StringUtils.defaultIfBlank(node.getString("time"), ""));
            vo.setDesc(statusText);
            vo.setActive(i == 0);
            tracks.add(vo);
        }
        return tracks;
    }

    private String resolveAppCode() {
        return StringUtils.trimToEmpty(ConfigUtil.getConfigValue(SysConfigConstant.AliyunLogistics.APP_CODE));
    }

    static boolean isSfExpress(String expressNo) {
        String no = StringUtils.upperCase(StringUtils.trimToEmpty(expressNo));
        return no.startsWith("SF") || no.startsWith("S.F");
    }

    static String extractLast4(String phone) {
        String digits = StringUtils.trimToEmpty(phone).replaceAll("\\D", "");
        if (digits.length() < 4) {
            return null;
        }
        return digits.substring(digits.length() - 4);
    }

    private static List<String> collectLast4(String... phones) {
        Set<String> uniq = new LinkedHashSet<>();
        if (phones == null) {
            return new ArrayList<>();
        }
        for (String phone : phones) {
            String last4 = extractLast4(phone);
            if (last4 != null) {
                uniq.add(last4);
            }
        }
        return new ArrayList<>(uniq);
    }

    private static boolean isPhoneTailRequired(String message) {
        if (StringUtils.isBlank(message)) {
            return false;
        }
        return message.contains("手机尾号")
                || message.contains("手机号后四位")
                || message.contains("4位手机号")
                || message.contains("尾号4位")
                || message.contains("尾号四位");
    }

    /**
     * 部分承运商会在 number 字段返回「电话:单号」形式，截取冒号前电话并写回
     */
    private static void normalizeCourierPhone(JSONObject result) {
        String number = result.getString("number");
        if (StringUtils.isBlank(number) || !number.contains(":")) {
            return;
        }
        Matcher matcher = COURIER_PHONE_PREFIX.matcher(number);
        if (matcher.find()) {
            result.put("number", matcher.group(1));
        }
    }

    private static String truncateTitle(String remark) {
        if (StringUtils.length(remark) <= 16) {
            return remark;
        }
        return remark.substring(0, 16);
    }
}

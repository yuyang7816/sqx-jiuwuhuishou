package com.sqx.app.settlement.service;

import com.sqx.app.settlement.domain.bo.SettlementConfirmBo;
import com.sqx.app.settlement.domain.bo.SettlementDisputeBo;
import com.sqx.app.settlement.domain.bo.SettlementInfoBo;
import com.sqx.app.settlement.domain.vo.SettlementConfirmVo;
import com.sqx.app.settlement.domain.vo.SettlementInfoVo;

public interface SettlementService {

    SettlementInfoVo info(Long userId, SettlementInfoBo infoBo);

    SettlementConfirmVo confirm(Long userId, SettlementConfirmBo confirmBo);

    void dispute(Long userId, SettlementDisputeBo disputeBo);
}

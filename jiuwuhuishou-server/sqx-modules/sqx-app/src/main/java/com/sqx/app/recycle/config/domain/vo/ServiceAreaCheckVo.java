package com.sqx.app.recycle.config.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 服务区域校验结果
 */
@Data
public class ServiceAreaCheckVo {

    @Schema(description = "是否在服务范围内")
    private Boolean inService;
}

package com.sqx.app.admin.recycle.appliance.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminApplianceHeroFlowPageBo extends PageParam {

    @Schema(description = "步骤名称")
    private String name;

    @Schema(description = "状态 1上架 2下架")
    private Integer state;
}

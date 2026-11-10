package com.sqx.app.admin.pickup.domain.vo;

import com.sqx.app.pickup.domain.vo.PickupTaskLogVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminPickupTaskDetailVo extends AdminPickupTaskVo {

    @Schema(description = "流程节点")
    private List<PickupTaskLogVo> logs;
}

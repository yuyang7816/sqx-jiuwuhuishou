package com.sqx.app.express.sf.domain;

import lombok.Data;

/**
 * 顺丰路由节点
 */
@Data
public class SfRouteNode {
    private String acceptTime;
    private String acceptAddress;
    private String remark;
    private String opCode;
}

package com.example.entity;

import lombok.Data;

@Data
public class FunctionPoint {

    private Integer subsystemId;    // 子系统ID
    private Integer projectId;      // 项目ID
    private Integer featureId;      // 功能点ID
    private String featureName;     // 功能点名称
    private String complexity;      // 重用程度
    private String modifyType;      // 修改类型
    private String featureType;     // 功能类型
    private String note;            // 备注

}

package com.example.entity;
import lombok.Data;

@Data
public class Detail {
    private Integer projectId;      //项目ID
    private String subsystemName;        // 子系统名称
    private String subsystemDescribe;    // 子系统描述
    private Double unadjustedFp;         // 未调整功能点
    private Double adjustedFp;           // 调整后功能点
    private String featureName;     // 功能点名称
    private String complexity;      // 重用程度
    private String modifyType;      // 修改类型
    private String featureType;     // 功能类型
    private String note;            // 备注

//    public void setUnadjustedFp(Double unadjustedFp) {
//        unadjustedFp = 0.0;
//        this.unadjustedFp = unadjustedFp;
//    }
//
//    public void setAdjustedFp(Double adjustedFp) {
//        adjustedFp = 0.0;
//        this.adjustedFp = adjustedFp;
//    }
//
//    public Detail(Double unadjustedFp, Double adjustedFp) {
//        this.unadjustedFp = unadjustedFp;
//        this.adjustedFp = adjustedFp;
//    }
}

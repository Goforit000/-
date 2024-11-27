package com.example.entity;

import lombok.Data;

import java.util.Objects;

@Data
public class Subsystem {

    private Integer subsystemId;         // 子系统ID
    private Integer projectId;           // 项目ID
    private String subsystemName;        // 子系统名称
    private String subsystemDescribe;    // 子系统描述
    private Double unadjustedFp;         // 未调整功能点
    private Double adjustedFp;           // 调整后功能点
    private String adjustedMethod;       // 调整方法

    // 新增一个方法来更新 unadjustedFp
    public void update(Double unadjustedFp,double adjustedFp,String adjustedMethod) {
        this.unadjustedFp = unadjustedFp;
        this.adjustedFp = adjustedFp;
        this.adjustedMethod = adjustedMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subsystem subsystem = (Subsystem) o;
        return Objects.equals(subsystemName, subsystem.subsystemName) &&
                Objects.equals(subsystemDescribe, subsystem.subsystemDescribe) &&
                Objects.equals(unadjustedFp, subsystem.unadjustedFp) &&
                Objects.equals(adjustedFp, subsystem.adjustedFp) &&
                Objects.equals(adjustedMethod, subsystem.adjustedMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subsystemName, subsystemDescribe, unadjustedFp, adjustedFp, adjustedMethod);
    }
     // Getter 方法
    public Double getAdjustedFp() {
        return adjustedFp != null ? adjustedFp : 0.0; // 返回默认值，避免空指针
    }

    public Double getUnadjustedFp() {
        return unadjustedFp != null ? unadjustedFp : 0.0; // 返回默认值，避免空指针
    }
}

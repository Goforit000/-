package com.example.entity;
import lombok.Data;

@Data
public class CostEvaluation {
    private Integer projectId;
    private String projectName;
    private Float adjustedFP;
    private Float upperPDR;
    private Float midPDR;
    private Float floorPDR;
    private Float businessDomain;
    private Float applicationType;
    private Float qualityCharacteristic;
    private Float developLanguage;
    private Float teamBackground;
    private Float upperWorkload;
    private Float midWorkload;
    private Float floorWorkload;
    private Float risk;
    private Float upperCost;
    private Float midCost;
    private Float floorCost;
    private Float upperAdjustedCost;
    private Float midAdjustedCost;
    private Float floorAdjustedCost;
    private Float directNonhumanCost;
    private Float straightQualityGrade;
    private Float laborCostRate;
    private int bigProjectId;

    private int subsystemId;
    private String subsystemName;
    private String subsystemDescribe;
    private double unadjustedFp;
    private double adjustedFp;
    private String adjustedMethod;

    public double getAdjustedFp() {
        return adjustedFp;
    }

    public void setAdjustedFp(double adjustedFp) {
        this.adjustedFp = adjustedFp;
    }

    public Float getAdjustedFP() {
        return adjustedFP;
    }

    public void setAdjustedFP(Float adjustedFP) {
        this.adjustedFP = adjustedFP;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

}

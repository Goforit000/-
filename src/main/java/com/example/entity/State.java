package com.example.entity;
import lombok.Data;
@Data
public class State {
    private int stateId;
    private String stateName;
    private int applicationTypeState;
    private int qualityCharacteristicState;
    private int developLanguageState;
    private int riskState;
    private int directNonhumanCostState;
    private int straightQualityGradeState;
    private int teamBackground;
    private String stateDetail;
    private int isOfficial;
    private int isAvailable;
}

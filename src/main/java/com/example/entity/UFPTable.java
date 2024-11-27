package com.example.entity;

public class UFPTable {
    public static final int LOW = 0;     // 低
    public static final int MEDIUM = 1;  // 中
    public static final int HIGH = 2;    // 高

    public static final int ILF_UFP = 0;
    public static final int EIF_UFP = 1;
    public static final int EI_UFP = 2;
    public static final int EO_UFP = 3;
    public static final int EQ_UFP = 4;

    private static final int[][] UFP_VALUES = {
            {7, 5, 3, 4, 3},   // 低
            {10, 7, 4, 5, 4},  // 中
            {15, 10, 6, 7, 6}  // 高
    };

    public static int getValue(int complexity, int type) {
        return UFP_VALUES[complexity][type];
    }

    public static int searchUFP(String complexity, String type) {
        int complexityIndex;
        int typeIndex;
        complexityIndex = switch (complexity) {
            case "低" -> 0;
            case "中" -> 1;
            case "高" -> 2;
            default -> -1;
        };

        typeIndex = switch (type) {
            case "ILF" -> 0;
            case "EIF" -> 1;
            case "EI" -> 2;
            case "EO" -> 3;
            case "EQ" -> 4;
            default -> -1;
        };

        return getValue(complexityIndex, typeIndex);
    }
}


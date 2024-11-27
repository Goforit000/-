package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data // Lombok 会自动生成 getter, setter, toString, equals, hashCode 等方法
public class Project {

    private int projectId;               // 项目ID
    private String projectName;           // 项目名称
    private Double totalAdjustedFp;       // 总调整功能点数
    private Double totalUnadjustedFp;    // 总未调整功能点数
    private String projectDescribe;       // 项目描述
    private LocalDateTime startTime;      // 项目开始时间
    private LocalDateTime endTime;        //项目结束时间
    private String note;
    private String status;

//    public Project(Double totalAdjustedFp, Double totalUnadjustedFp) {
//        totalAdjustedFp = 0.0;
//        totalUnadjustedFp = 0.0;
//        this.totalAdjustedFp = totalAdjustedFp;
//        this.totalUnadjustedFp = totalUnadjustedFp;
//    }
}

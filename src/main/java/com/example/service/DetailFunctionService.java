package com.example.service;

import com.example.entity.Detail;
import com.example.entity.Subsystem;
import com.example.mapper.DetailFunctionMapper;
import com.example.mapper.SubsystemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DetailFunctionService {

    @Autowired
    private DetailFunctionMapper detailMapper; // DAO 层
    @Autowired
    private SubsystemMapper subsystemMapper;

    /**
     * 获取所有功能点
     * @return 功能点列表
     */
    public List<Detail> getAll(int projectId) {
        return detailMapper.findAllDetails(projectId);
    }
    public double getPrePoint(int projectId) {
//        System.out.println(projectId);
        List<Subsystem> subsystems = subsystemMapper.selectByProjectId(projectId);
        double totalUnadjustedFp =  0;
        for (Subsystem subsystem : subsystems){
            totalUnadjustedFp += subsystem.getUnadjustedFp();
        }
        detailMapper.updateTotalUnadjustedFp(projectId,totalUnadjustedFp);
        System.out.println("总未调整功能点数："+totalUnadjustedFp);
        return totalUnadjustedFp;
    }
    public double getAdjPoint(int projectId) {
        List<Subsystem> subsystems = subsystemMapper.selectByProjectId(projectId);
        double totalAdjustedFp =  0;
        for (Subsystem subsystem : subsystems){
            totalAdjustedFp += subsystem.getAdjustedFp();
        }
        detailMapper. updateTotalAdjustedFp(projectId,totalAdjustedFp);
        System.out.println("总调整后功能点数："+totalAdjustedFp);
        return totalAdjustedFp;
    }


    public void insertDetails(int projectId, List<Detail> details) {

        // Step 1: 处理每个 Detail 数据
        Map<String, Integer> subsystemMap = new HashMap<>(); // 用来缓存子系统名到subsystem_id的映射

        for (Detail detail : details) {
            // Step 2: 处理 Subsystem（子系统）数据
            String subsystemName = detail.getSubsystemName();
            String subsystemDescribe = detail.getSubsystemDescribe();

            // 如果subsystemMap中已经包含该subsystemName，则使用现有的subsystemId
            if (!subsystemMap.containsKey(subsystemName)) {
                Subsystem subsystem = new Subsystem();
                subsystem.setSubsystemName(subsystemName);
                subsystem.setSubsystemDescribe(subsystemDescribe);
                subsystem.setProjectId(projectId); // 设定项目ID

                // 插入子系统数据并获取subsystem_id
                System.out.println("insertSubsystem service --> mapper");
                System.out.println(subsystem);
                detailMapper.insertSubsystem(projectId, subsystem);

                System.out.println("costevaluation前");
                detailMapper.insertCostEvaluation(projectId,subsystem);
                System.out.println("costevaluation后");
                // 这里我们假设插入成功后，subsystemId会被自动回填
                subsystemMap.put(subsystemName, subsystem.getSubsystemId());
            }

            // Step 3: 处理 Function Points（功能点）数据
            int subsystemId = subsystemMap.get(subsystemName); // 获取子系统ID
            System.out.println("AI子系统id: "+subsystemId);
            String featureName = detail.getFeatureName();
            String complexity = detail.getComplexity();
            String modifyType = detail.getModifyType();
            String featureType = detail.getFeatureType();
            String note = detail.getNote();

            // 插入功能点数据
            System.out.println("insertFunction service --> mapper");
            System.out.println(subsystemId+featureName+ complexity+ modifyType+ featureType+ note+projectId);
            detailMapper.insertFunctionPoint(subsystemId, featureName, complexity, modifyType, featureType, note, projectId);

            System.out.println("ai service 完毕");
        }
    }


}

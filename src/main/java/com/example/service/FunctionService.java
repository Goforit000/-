package com.example.service;

import com.example.entity.FunctionPoint;
import com.example.entity.Subsystem;
import com.example.entity.UFPTable;
import com.example.mapper.FunctionMapper;
import com.example.mapper.SubsystemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class FunctionService {

    @Autowired
    private FunctionMapper functionMapper;
    @Autowired
    private SubsystemMapper subsystemMapper;

    // 获取子系统ID对应的功能点
    public List<FunctionPoint> getFunctionPointsBySubsystemId(Integer subsystemId) {
        System.out.println("功能点service:"+functionMapper.findFunctionPointsBySubsystemId(subsystemId));
        double unadjustedFp = 0;
        List<FunctionPoint> functionPoints = functionMapper.findFunctionPointsBySubsystemId(subsystemId);
        for (FunctionPoint fp : functionPoints) {
            int ufp = UFPTable.searchUFP(fp.getComplexity(), fp.getFeatureType());
            unadjustedFp += ufp;
        }
//        Subsystem subsystem = subsystemMapper.selectById(subsystemId,);
//        if (subsystem != null) {
//            subsystem.update(unadjustedFp,adjustedFp,adjustedMethod); // 更新 unadjustedFp
//            subsystemMapper.updateSubsystem(subsystem); // 持久化到数据库
        return functionMapper.findFunctionPointsBySubsystemId(subsystemId);
    }

    // 批量保存功能点逻辑
    public boolean saveFunctionPoints(Integer projectId, Integer subsystemId, List<FunctionPoint> functionPoints) {
        // 设置功能点的项目ID和子系统ID
        for (FunctionPoint functionPoint : functionPoints) {
            functionPoint.setProjectId(projectId);
            functionPoint.setSubsystemId(subsystemId);
        }

        boolean success = true;

        // 遍历每个功能点，检查是否存在
        for (FunctionPoint functionPoint : functionPoints) {
            // 检查功能点是否存在
            boolean exists = functionMapper.existsFunctionPoint(functionPoint.getFeatureId(), projectId, subsystemId);

            if (exists) {
                // 如果存在，检查是否需要更新
                FunctionPoint existingFunctionPoint = functionMapper.selectByFeatureId(functionPoint.getFeatureId(), projectId, subsystemId);
                if (existingFunctionPoint != null && !existingFunctionPoint.equals(functionPoint)) {
                    // 如果功能点内容有变动，更新功能点
                    int updatedRows = functionMapper.updateFunctionPoint(functionPoint);
                    if (updatedRows != 1) {
                        success = false;  // 更新失败
                    }
                }
            } else {
                // 如果功能点不存在，插入新功能点
                int insertedRows = functionMapper.insertFunctionPoint(functionPoint);
                if (insertedRows != 1) {
                    success = false;  // 插入失败
                }
            }
        }

        return success;
    }

    // 提交调整方法
    public boolean submitAdjustmentMethod(Integer projectId, Integer subsystemId, String adjustedMethod) {
        // 获取对应的 Subsystem 实体并更新 unadjustedFp
        System.out.println("调整方法service");
        Subsystem subsystem = subsystemMapper.selectById(subsystemId,projectId); // 通过 subsystemId 查找子系统
        List<FunctionPoint> functionPoints = functionMapper.selectByProjectIdAndSubsystemId(projectId,subsystemId);
        System.out.println("调整的子系统："+subsystem);
        double unadjustedFp = 0;
        double adjustedFp = 0;
        double CFP = 0;
        double CF = 2;
        // 计算未调整的功能点
        for (FunctionPoint fp : functionPoints) {
            int ufp = UFPTable.searchUFP(fp.getComplexity(), fp.getFeatureType());
            unadjustedFp += ufp;
        }

        if(adjustedMethod.equals("GSC")) {
            double VAF = calDISum() * 0.01 + 0.65;
            adjustedFp = (unadjustedFp + CFP) * VAF;
        }else {
            adjustedFp = unadjustedFp * CF;
        }

        if (subsystem != null) {
            subsystem.update(unadjustedFp,adjustedFp,adjustedMethod); // 更新 unadjustedFp
            subsystemMapper.updateSubsystem(subsystem); // 持久化到数据库
                   return true;
        } else {
            return false; // 如果没有找到对应的子系统
        }

    }

    // 目前DI是随机生成的
    private int calDISum() {
        Random random = new Random();
        int sum = 0;

        for (int i = 0; i < 15; i++) {
            int randomNumber = random.nextInt(6);
            sum += randomNumber;
        }

        return sum;
    }


    // 删除功能点
    public boolean deleteFunctionPoint(Integer projectId, Integer subsystemId, Integer featureId) {
        // 通过功能点 ID 查找是否存在
        boolean exists = functionMapper.existsFunctionPoint(featureId,projectId, subsystemId);
        if (!exists) {
            return false;  // 如果功能点不存在，返回 false
        }
        // 执行删除操作
        int rowsAffected = functionMapper.deleteFunctionPoint(featureId, subsystemId);
        return rowsAffected > 0;  // 如果影响的行数大于0，表示删除成功
    }

}

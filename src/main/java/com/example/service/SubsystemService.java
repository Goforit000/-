package com.example.service;

import com.example.entity.Subsystem;
import com.example.mapper.SubsystemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class SubsystemService {

    @Autowired
    private SubsystemMapper subsystemMapper;

    // 根据项目ID获取子系统列表
    public List<Subsystem> getSubsystemsByProjectId(Integer projectId) {
        System.out.println("service获取子系统" + subsystemMapper.selectByProjectId(projectId));
        return subsystemMapper.selectByProjectId(projectId);
    }

    // 删除子系统
    public boolean deleteSubsystem(Integer projectId, Integer subsystemId) {
        int rowsAffected = subsystemMapper.delete(subsystemId, projectId); // 注意：项目ID和子系统ID顺序一致
        return rowsAffected > 0;  // 如果删除成功，返回 true
    }
    // 获取某个子系统
    public Subsystem fetchSubsystem(Integer subsystemId, Integer projectId) {
        return subsystemMapper.selectById(subsystemId, projectId);
    }

    // 批量保存子系统
    public boolean saveSubsystems(Integer projectId, List<Subsystem> subsystems) {
        // 确保每个子系统都设置了正确的项目 ID
        for (Subsystem subsystem : subsystems) {
            subsystem.setProjectId(projectId);
        }
        boolean success = true;
        // 遍历所有子系统
        for (Subsystem subsystem : subsystems) {
            boolean exists = subsystemMapper.existsSubsystem(subsystem.getSubsystemId(), projectId);

            if (exists) {
                System.out.println("subsystemId: " + subsystem.getSubsystemId());
                System.out.println("projectId: " + projectId);

                // 如果子系统已存在，检查内容是否发生变化
                Subsystem existingSubsystem = subsystemMapper.selectById(subsystem.getSubsystemId(), projectId);
                System.out.println("已存在子系统："+existingSubsystem);
                // 如果内容有变化，执行更新
                if (existingSubsystem != null && !existingSubsystem.equals(subsystem)) {
                    int updatedRows = subsystemMapper.updateSubsystem(subsystem);
                    System.out.println("更新后："+updatedRows);
                    if (updatedRows != 1) {
                        success = false;  // 更新失败
                    }
                }
            } else {
                // 子系统不存在，进行插入
                int insertedRows = subsystemMapper.insertSubsystem(subsystem);

                 subsystemMapper.insertCostEvaluation(subsystem);
                if (insertedRows != 1) {
                    success = false;  // 插入失败
                }
            }
        }

        return success;
    }







}

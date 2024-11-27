package com.example.service;

import com.example.entity.CostEvaluation;
import com.example.entity.State;
import com.example.mapper.CostEvaluationMapper;
import com.example.mapper.StateMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class CostEvaluationService {

    @Autowired
    private CostEvaluationMapper costEvaluationMapper;
    @Autowired
    private StateMapper stateMapper;

    private State state;

     // 根据 bigProjectId 查询子系统列表
    public List<CostEvaluation> getSubsystemsByBigProjectId(int bigProjectId) {
//        costEvaluationMapper.insert();
        return costEvaluationMapper.selectByBigProjectId(bigProjectId);
    }

    // 根据 bigProjectId 查询对应的子系统数据
    public List<CostEvaluation> getSubsystemByBigProjectId(int bigProjectId) {
        return costEvaluationMapper.selectSubsystemByBigProjectId(bigProjectId);
    }
     // 获取所有成本评估
    public List<CostEvaluation> getAllCostEvaluations() {
        return costEvaluationMapper.selectAll();  // 调用Mapper获取所有成本评估
    }
    /**
     * 根据项目ID获取成本评估数据
     *
     * @param projectId 项目ID
     * @return 成本评估对象
     */
    public CostEvaluation getCostEvaluationById(int projectId) {
        System.out.println("查询成本评估，项目ID: " + projectId);  // 调试：输出查询的项目ID
        List<CostEvaluation> costEvaluations = costEvaluationMapper.selectSubsystemById(projectId);
        CostEvaluation costEvaluation = costEvaluations.get(0);
        if (costEvaluation == null) {
            throw new IllegalArgumentException("Project ID不存在");
        }
        System.out.println("查询结果: " + costEvaluation);  // 调试：输出查询结果
        return costEvaluation;
    }

    /**
     * 计算并保存成本评估数据
     *
     * @param costEvaluation 成本评估数据
     * @return 更新后的成本评估数据
     */
    public CostEvaluation calculateAndSaveCostEvaluation(CostEvaluation costEvaluation) {
        System.out.println("接收到的成本评估数据: " + costEvaluation);  // 调试：输出接收到的数据

        // 从数据库获取项目评估数据
        List<CostEvaluation> dbCostEvaluations = costEvaluationMapper.selectSubsystemById(costEvaluation.getProjectId());
        if (dbCostEvaluations.isEmpty()) {
            throw new IllegalArgumentException("Project ID不存在");
        }
        // 只取第一条记录
        CostEvaluation dbCostEvaluation = dbCostEvaluations.get(0);


        // 获取subsystem表中的adjusted_fp并赋值给cost_evaluation表中的adjustedFP
        Double adjustedFpFromSubsystem = dbCostEvaluation.getAdjustedFp();  // 获取subsystem表中的adjusted_fp
        if (adjustedFpFromSubsystem != null) {
            costEvaluation.setAdjustedFP(adjustedFpFromSubsystem.floatValue());  // 将其赋值给cost_evaluation中的adjustedFP
        }


        // 根据状态重设因子
        if (state != null) {
            if(state.getApplicationTypeState() == 0) {
                costEvaluation.setApplicationType(1.0f);
            }
            if(state.getDevelopLanguageState() == 0) {
                costEvaluation.setDevelopLanguage(1.0f);
            }
            if(state.getRiskState() == 0) {
                costEvaluation.setRisk(1.0f);
            }
            if(state.getDirectNonhumanCostState() == 0) {
                costEvaluation.setDirectNonhumanCost(0f);
            }
            if(state.getQualityCharacteristicState() == 0) {
                costEvaluation.setQualityCharacteristic(1.0f);
            }
            if(state.getStraightQualityGradeState() == 0) {
                costEvaluation.setStraightQualityGrade(1.0f);
            }
        } else {
            throw new IllegalStateException("State object is not initialized.");
        }


        // 调试：输出数据库中的成本评估数据
        System.out.println("数据库中的成本评估数据: " + dbCostEvaluation);

        // 计算工作量（Workload）
        float upperWorkload = costEvaluation.getUpperPDR() * dbCostEvaluation.getAdjustedFP()
                * costEvaluation.getBusinessDomain() * costEvaluation.getApplicationType()
                * costEvaluation.getQualityCharacteristic() * costEvaluation.getDevelopLanguage()
                * costEvaluation.getTeamBackground();
        System.out.println("Upper Workload: " + upperWorkload);  // 调试：输出 Upper Workload

        float midWorkload = costEvaluation.getMidPDR() * dbCostEvaluation.getAdjustedFP()
                * costEvaluation.getBusinessDomain() * costEvaluation.getApplicationType()
                * costEvaluation.getQualityCharacteristic() * costEvaluation.getDevelopLanguage()
                * costEvaluation.getTeamBackground();
        System.out.println("Mid Workload: " + midWorkload);  // 调试：输出 Mid Workload

        float floorWorkload = costEvaluation.getFloorPDR() * dbCostEvaluation.getAdjustedFP()
                * costEvaluation.getBusinessDomain() * costEvaluation.getApplicationType()
                * costEvaluation.getQualityCharacteristic() * costEvaluation.getDevelopLanguage()
                * costEvaluation.getTeamBackground();
        System.out.println("Floor Workload: " + floorWorkload);  // 调试：输出 Floor Workload

        // 更新工作量到成本评估对象
        costEvaluation.setUpperWorkload(upperWorkload);
        costEvaluation.setMidWorkload(midWorkload);
        costEvaluation.setFloorWorkload(floorWorkload);

        // 计算成本（Cost）
        float upperCost = upperWorkload * costEvaluation.getLaborCostRate() + costEvaluation.getDirectNonhumanCost();
        float midCost = midWorkload * costEvaluation.getLaborCostRate() + costEvaluation.getDirectNonhumanCost();
        float floorCost = floorWorkload * costEvaluation.getLaborCostRate() + costEvaluation.getDirectNonhumanCost();
        System.out.println("Upper Cost: " + upperCost);  // 调试：输出 Upper Cost
        System.out.println("Mid Cost: " + midCost);    // 调试：输出 Mid Cost
        System.out.println("Floor Cost: " + floorCost);  // 调试：输出 Floor Cost

        // 更新成本到成本评估对象
        costEvaluation.setUpperCost(upperCost);
        costEvaluation.setMidCost(midCost);
        costEvaluation.setFloorCost(floorCost);

        // 计算调整后的成本（Adjusted Cost）
        float upperAdjustedCost = upperCost * costEvaluation.getRisk() * costEvaluation.getStraightQualityGrade();
        float midAdjustedCost = midCost * costEvaluation.getRisk() * costEvaluation.getStraightQualityGrade();
        float floorAdjustedCost = floorCost * costEvaluation.getRisk() * costEvaluation.getStraightQualityGrade();
        System.out.println("Upper Adjusted Cost: " + upperAdjustedCost);  // 调试：输出 Upper Adjusted Cost
        System.out.println("Mid Adjusted Cost: " + midAdjustedCost);    // 调试：输出 Mid Adjusted Cost
        System.out.println("Floor Adjusted Cost: " + floorAdjustedCost);  // 调试：输出 Floor Adjusted Cost

        // 更新调整后的成本到成本评估对象
        costEvaluation.setUpperAdjustedCost(upperAdjustedCost);
        costEvaluation.setMidAdjustedCost(midAdjustedCost);
        costEvaluation.setFloorAdjustedCost(floorAdjustedCost);

        System.out.println("更新的项目ID: " + costEvaluation.getProjectId());

        // 将结果保存到数据库
        int rowsUpdated = costEvaluationMapper.update(costEvaluation);
        if (rowsUpdated > 0) {
            System.out.println("成本评估数据保存成功");  // 调试：输出保存成功的提示
            return costEvaluation;
        } else {
            throw new RuntimeException("保存到数据库失败");
        }
    }

    /**
     * 删除成本评估数据
     *
     * @param projectId 项目ID
     * @return 删除的行数
     */
    public int deleteCostEvaluation(int projectId) {
        System.out.println("删除成本评估，项目ID: " + projectId);  // 调试：输出删除的项目ID
        return costEvaluationMapper.delete(projectId);
    }

    // 1. 获取所有状态的 stateId 和 stateName
    public List<State> getAllStates() {
        return stateMapper.getAllStates();
    }

    // 2. 根据 stateId 加载状态数据并保存到内存
    public State loadStateById(int stateId) {
    State state = stateMapper.getStateById(stateId);
    if (state == null) {
        throw new IllegalStateException("State not found for ID: " + stateId);
    }
    this.state = state;  // 如果状态存在，保存到内存中
    return state;
}


    // 3. 更新数据库中的状态，并更新内存中的状态
    public boolean updateState(State newState) {
        // 先检查数据库中是否存在该状态（通过 stateId）
        State existingState = stateMapper.getStateById(newState.getStateId());

        if (existingState != null) {
            // 更新数据库中的状态
            stateMapper.updateState(newState);
            // 更新内存中的状态
            this.state = newState;
            return true;
        }
        return false; // 如果没有找到该状态，返回 false
    }

    // 4. 新增状态，如果 stateId 已存在，则返回 false
    public boolean addState(State newState) {
        // 检查是否已经存在该 stateId
        State existingState = stateMapper.getStateById(newState.getStateId());
        if (existingState == null) {
            // 插入新的状态
            stateMapper.insertState(newState);
            // 更新内存中的状态
            this.state = newState;
            return true;
        }
        return false; // 如果已经存在该 stateId，则返回 false
    }

    // 5. 删除指定 stateId 的状态
    public boolean deleteState(int stateId) {
        // 删除状态
        int rowsAffected = stateMapper.deleteState(stateId);
        return rowsAffected > 0;
    }

}

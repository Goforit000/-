package com.example.controller;

import com.example.entity.State;
import com.example.entity.StateRequest;
import com.example.service.CostEvaluationService;
import com.example.entity.CostEvaluation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/cost-evaluation")
public class CostEvaluationController {

    @Autowired
    private CostEvaluationService costEvaluationService;

    @GetMapping("/sub/{bigProjectId}")
    public List<CostEvaluation> getByBigProjectId(@PathVariable int bigProjectId) {
        return costEvaluationService.getSubsystemsByBigProjectId(bigProjectId);
    }
    @GetMapping("/subsystems/{bigProjectId}")
    public List<CostEvaluation> getSubsystemsByBigProjectId(@PathVariable int bigProjectId) {
        // 调用 Service 层获取数据
        return costEvaluationService.getSubsystemByBigProjectId(bigProjectId);
    }

    // 获取所有成本评估
    @GetMapping("/all")
    public List<CostEvaluation> getAllCostEvaluations() {
        List<CostEvaluation> evaluations = costEvaluationService.getAllCostEvaluations();
        evaluations.forEach(System.out::println);  // 打印每个成本评估对象
        return evaluations;
    }
    // 获取项目成本评估
    @GetMapping("/{projectId}")
    public CostEvaluation getCostEvaluation(@PathVariable int projectId) {
        return costEvaluationService.getCostEvaluationById(projectId);
    }

    // 添加成本评估
    @PostMapping("/add")
    public CostEvaluation addCostEvaluation(@RequestBody CostEvaluation costEvaluation) {
        System.out.println("接收到的成本评估数据: " + costEvaluation);  // 调试：输出接收到的数据
        return costEvaluationService.calculateAndSaveCostEvaluation(costEvaluation);
    }

    // 更新成本评估
    @PutMapping("/update")
    public CostEvaluation updateCostEvaluation(@RequestBody CostEvaluation costEvaluation) {
        return costEvaluationService.calculateAndSaveCostEvaluation(costEvaluation);
    }

    // 删除成本评估
    @DeleteMapping("/delete/{projectId}")
    public int deleteCostEvaluation(@PathVariable int projectId) {
        return costEvaluationService.deleteCostEvaluation(projectId);
    }

     // 计算成本评估并返回结果
    @PostMapping("/calculate")
    public CostEvaluation calculateCostEvaluation(@RequestBody CostEvaluation costEvaluation) {
        System.out.println("接收到的成本评估数据: " + costEvaluation);  // 调试：输出接收到的数据
        return costEvaluationService.calculateAndSaveCostEvaluation(costEvaluation);  // 调用计算并保存方法
    }
    // 1. 获取所有状态的 stateId 和 stateName
    @GetMapping("/getAllStates")
    public List<State> getAllStates() {
        return costEvaluationService.getAllStates();
    }

    // 2. 根据 stateId 获取状态并更新后端存储的状态
    @GetMapping("/getStateById/{stateId}")
    public State getStateById(@PathVariable int stateId) {
        return costEvaluationService.loadStateById(stateId);
    }

    // 3. 更新数据库中的状态，前端传递包含 stateId 的完整状态数据
    @PostMapping("/updateState")
    public String updateState(@RequestBody State newState) {
        boolean isUpdated = costEvaluationService.updateState(newState);

        if (isUpdated) {
            return "State updated successfully.";
        } else {
            return "Failed to update the state.";
        }
    }

    // 4. 新增状态，如果 stateId 已存在，返回"已存在"
    @PostMapping("/addState")
    public String addState(@RequestBody State newState) {
        boolean isAdded = costEvaluationService.addState(newState);
        if (isAdded) {
            return "State added successfully.";
        } else {
            return "State with the same stateId already exists./State added unsuccessfully.";
        }
    }

    // 5. 根据 stateId 删除状态
    @DeleteMapping("/deleteState/{stateId}")
    public String deleteState(@PathVariable int stateId) {
        boolean isDeleted = costEvaluationService.deleteState(stateId);
        if (isDeleted) {
            return "State deleted successfully.";
        } else {
            return "State not found.";
        }
    }
}

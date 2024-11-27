package com.example.controller;


import com.example.entity.FunctionPoint;
import com.example.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/{projectId}/subsystems")
public class FunctionController {

    @Autowired
    private FunctionService functionService;

    // 根据子系统名称获取功能点
    @GetMapping("/{subsystemId}/functions")
    public ResponseEntity<List<FunctionPoint>> getFunctionPointsBySubsystemId(@PathVariable Integer subsystemId) {
        System.out.println("获取子系统ID： " + subsystemId + "的功能点");

        List<FunctionPoint> functionPoints = functionService.getFunctionPointsBySubsystemId(subsystemId);
        System.out.println("功能点controller："+functionPoints);
        if (functionPoints.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(functionPoints);
    }
    // 批量保存子系统
    @PostMapping("/{subsystemId}/save")
    public ResponseEntity<Void> saveFunctions(@PathVariable Integer projectId,@PathVariable Integer subsystemId, @RequestBody List<FunctionPoint> functionPoints) {
        System.out.println("保存功能点:"+functionPoints);
        boolean isSaved = functionService.saveFunctionPoints(projectId,subsystemId, functionPoints);
        if (isSaved) {
            return ResponseEntity.ok().build();  // 返回 200 OK 表示保存成功
        } else {
            return ResponseEntity.status(500).build();  // 返回 500 Internal Server Error 表示保存失败
        }
    }

    @DeleteMapping("/{subsystemId}/{featureId}")
    // 删除功能点
    public ResponseEntity<Void> deleteFunctionPoint(
            @PathVariable Integer projectId,
            @PathVariable Integer subsystemId,
            @PathVariable Integer featureId) {
        System.out.println("删除功能点"+projectId+subsystemId+featureId);
        boolean isDeleted = functionService.deleteFunctionPoint(projectId, subsystemId, featureId);

        if (isDeleted) {
            return ResponseEntity.noContent().build();  // 返回 204 No Content 表示删除成功
        } else {
            return ResponseEntity.notFound().build();  // 返回 404 Not Found 如果找不到对应的功能点
        }
    }
    // 批量保存子系统
    // 提交调整方法
    @PostMapping("/{subsystemId}/method")
    public ResponseEntity<Map<String, Object>> submitAdjustmentMethod(
            @PathVariable Integer projectId,
            @PathVariable Integer subsystemId,
            @RequestBody Map<String, String> request) {

        String adjustedMethod = request.get("method"); // 获取前端提交的调整方法
        System.out.println("调整方法"+adjustedMethod);
        boolean success = functionService.submitAdjustmentMethod(projectId, subsystemId, adjustedMethod);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);

        if (success) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}

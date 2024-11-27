package com.example.controller;

import com.example.entity.Subsystem;
import com.example.service.SubsystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{projectId}/subsystems")
public class SubsystemController {

    @Autowired
    private SubsystemService subsystemService;

    // 获取某项目的所有子系统
    @GetMapping("/fetch")
    public ResponseEntity<List<Subsystem>> getSubsystemsByProjectId(@PathVariable Integer projectId) {
        System.out.println("获取子系统"+projectId);
        List<Subsystem> subsystems = subsystemService.getSubsystemsByProjectId(projectId);
        if (subsystems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        System.out.println(subsystems);
        return ResponseEntity.ok(subsystems);
    }

    // 删除子系统
    @DeleteMapping("/{subsystemId}")
    public ResponseEntity<Void> deleteSubsystem(@PathVariable Integer projectId, @PathVariable Integer subsystemId) {
        boolean isDeleted = subsystemService.deleteSubsystem(projectId, subsystemId);
        if (isDeleted) {
            return ResponseEntity.noContent().build();  // 返回 204 No Content 表示成功删除
        } else {
            return ResponseEntity.notFound().build();  // 返回 404 Not Found 如果找不到对应的子系统
        }
    }

    // 批量保存子系统
    @PostMapping("/save")
    public ResponseEntity<Void> saveSubsystems(@PathVariable Integer projectId, @RequestBody List<Subsystem> subsystems) {
        System.out.println("保存子系统:"+subsystems);
        boolean isSaved = subsystemService.saveSubsystems(projectId, subsystems);
        if (isSaved) {
            return ResponseEntity.ok().build();  // 返回 200 OK 表示保存成功
        } else {
            return ResponseEntity.status(500).build();  // 返回 500 Internal Server Error 表示保存失败
        }
    }

    // 获取某个子系统
    @GetMapping("/{subsystemId}/adjustedMethod")
    public ResponseEntity<Subsystem> fetchSubsystemMethod(@PathVariable Integer projectId, @PathVariable Integer subsystemId) {
        Subsystem subsystem = subsystemService.fetchSubsystem( subsystemId,projectId);
        if (subsystem!=null) {
            return ResponseEntity.ok(subsystem);
        } else {
            // 如果没有找到子系统，返回 404 状态码
            return ResponseEntity.notFound().build();
        }
    }
}

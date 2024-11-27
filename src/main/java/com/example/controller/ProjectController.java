package com.example.controller;



import com.example.entity.Project;
import com.example.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @GetMapping("/listThird")
    public Map<String, Object> findAllThird() {
        System.out.println("111");
        List<Project> list = projectService.getProjectListThird();
        System.out.println(list);
        Map<String, Object> response = new HashMap<>();
        response.put("isOK", true);
        response.put("data", list);
        response.put("msg", "查询成功");
        return response;
        }
    @PostMapping("/add")
    public Map addProject(@RequestBody Project project) {
        System.out.println(project.getProjectName());
        System.out.println(project.getNote());
        boolean success = projectService.addProject(project);
        Map response = new HashMap<>();
        if (success){
            response.put("isOK", true);
            response.put("msg", "添加成功");
        }else {
            response.put("isOK", false);
            response.put("msg", "添加失败");
        }
        return response;
    }

    @GetMapping("/list")
    public Map<String, Object> findAll(@RequestParam int page, @RequestParam int size,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String status) {
        // 计算分页参数
        int offset = (page - 1) * size;

        // 调用服务层，传递分页参数
        List<Project> list = projectService.getProjectList(offset, size, name, status);

        // 查询总数
        int total = projectService.getTotalCount(name, status);

        // 构造响应
        Map<String, Object> response = new HashMap<>();
        response.put("isOK", true);
        response.put("data", list);
        response.put("total", total);
        response.put("msg", "查询成功");
        return response;
    }




    @DeleteMapping("/delete")
    public Map<String, Object> deleteProjectById(int projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isDeleted = projectService.deleteProjectById(projectId);
            if (isDeleted) {
                response.put("isOk", true);
                response.put("msg", "删除成功");
            } else {
                response.put("isOk", false);
                response.put("msg", "删除失败");
            }
        } catch (Exception e) {
            response.put("isOk", false);
            response.put("msg", "删除失败，发生异常：" + e.getMessage());
        }
        return response;
    }
  @RequestMapping("/update")
    public Map<String, Object> update(@RequestBody Project project) {

        boolean success = projectService.updateProject(project);
        Map<String, Object> map = new HashMap<>();
        if(success){
            map.put("isOK", true);
            map.put("msg", "更新成功");
        }else{
            map.put("isOK", false);
            map.put("msg", "更新失败");
        }
        return map;
    }
     @GetMapping("/search")
    public Map<String, Object> searchMeetings(String projectName) {
        List<Project> projects = projectService.searchByProjects(projectName);
        Map<String, Object> response = new HashMap<>();
        if (projects.isEmpty()) {
            response.put("isOK", false);
            response.put("msg", "没有找到符合条件的");
        } else {
            response.put("isOK", true);
            response.put("data", projects);
            response.put("msg", "搜索成功");
        }
        return response;
    }
}

package com.example.controller;


import com.example.entity.Detail;
import com.example.service.DetailFunctionService;
import com.example.util.AIConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.util.JsonParser.parseDetails;


@RestController
@RequestMapping("/detail")
public class DetailController {
    @Autowired
    private DetailFunctionService detailall;

    /**
     * 根据项目 ID 获取功能点数据
     *
     * @param projectId 项目 ID
     * @return 功能点数据列表
     */
    @GetMapping("/getall")
    public Map<String, Object> getFunctionsByProjectId(@RequestParam("projectId") int projectId) {

       List<Detail> list= detailall.getAll(projectId);

        Map<String, Object> response = new HashMap<>();
        response.put("isOK", true);
        response.put("data", list);
        response.put("msg", "查询成功");
        return response;
    }
    @GetMapping("/getpre")
    public Map<String, Object> getPrefpByProjectId(@RequestParam("projectId") int projectId){
        System.out.println("获取未调整");
      double prepoint= detailall.getPrePoint(projectId);
      Map<String, Object> response = new HashMap<>();
        response.put("isOK", true);
        response.put("data", prepoint);
        System.out.println(prepoint);
        response.put("msg", "查询成功");
        return response;
    }
     @GetMapping("/getadjusted")
    public Map<String, Object> getAdjustedfpByProjectId(@RequestParam("projectId") int projectId){
        System.out.println("获取调整");
      double prepoint= detailall.getAdjPoint(projectId);
      Map<String, Object> response = new HashMap<>();
        response.put("isOK", true);
        response.put("data", prepoint);
        System.out.println(prepoint);
        response.put("msg", "查询成功");
        return response;
    }

    @PostMapping("/ai")
    public void AIDetail(@RequestParam("projectId") int projectId,@RequestParam("fileName") String fileName) throws IOException {
        System.out.println("AIConfig前,文件名"+fileName);
        AIConfiguration.sendReq(fileName, "config_sum.txt", "result.txt");
        System.out.println("AIConfig后");
        String jsonPath = "./results/result.txt";
        List<Detail> details = parseDetails(jsonPath);
        System.out.println("controller AI 调用");
        detailall.insertDetails(projectId,details);
        System.out.println("controller AI 调用完毕");
//        Map<String, Object> response = new HashMap<>();
//        response.put("isOK", true);
//        response.put("data", prepoint);
//        System.out.println(prepoint);
//        response.put("msg", "查询成功");
//        return response;
    }

}

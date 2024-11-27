package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    public String uploadDir = "D:\\NEU\\third\\experiment\\combine\\frontwxq\\public";
    public String promptDir = "D:\\NEU\\third\\experiment\\combine\\endwxq\\prompts";  // 使用正斜杠
    ; // 新增目标目录

    // 文件上传接口
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("projectId") int projectId) {
        System.out.println(uploadDir);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("未接收到文件");
        }

        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (("word".equalsIgnoreCase(type) && !"docx".equalsIgnoreCase(fileExtension)) ||
                ("excel".equalsIgnoreCase(type) && !("xls".equalsIgnoreCase(fileExtension) || "xlsx".equalsIgnoreCase(fileExtension)))) {
            return ResponseEntity.badRequest().body("文件类型错误");
        }

        try {
            // 使用项目ID作为目录创建路径
            Path projectPath = Paths.get(uploadDir, String.valueOf(projectId));
            if (!Files.exists(projectPath)) {
                Files.createDirectories(projectPath);
            }

            // 获取文件的原始名称并生成新的文件名
            String originalFileName = StringUtils.stripFilenameExtension(file.getOriginalFilename());
            String fileName = String.format("%s.%s", originalFileName, fileExtension);

            Path targetPath = projectPath.resolve(fileName);
            System.out.println("targetPath" + targetPath);
            // 如果文件已经存在，返回文件已存在的提示
            if (Files.exists(targetPath)) {
                return ResponseEntity.ok(String.format("文件已存在: %s", fileName));
            }

            // 保存文件到磁盘
            Files.copy(file.getInputStream(), targetPath);

            // 新增功能：复制文件到指定的prompts目录
            Path promptsPath = Paths.get(promptDir, fileName);
            System.out.println("后端文件路径："+promptsPath);
            Files.copy(file.getInputStream(), promptsPath); // 复制文件到新的目录

            return ResponseEntity.ok(String.format("文件上传成功: %s (项目ID: %d)", fileName, projectId));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件上传失败");
        }
    }

    // 读取文件接口
    @GetMapping("/read")
    public ResponseEntity<?> readFileByProjectId(
            @RequestParam("projectId") int projectId,
            @RequestParam("fileName") String fileName) {

        // 使用项目ID和文件名构建文件路径
        Path filePath = Paths.get(uploadDir, String.valueOf(projectId), fileName);

        // 返回结果：传递文件的路径
        Map<String, Object> response = new HashMap<>();
        if (Files.exists(filePath)) {
            response.put("success", true);
            response.put("filePath", filePath.toString()); // 返回文件路径
        } else {
            response.put("success", false);
            response.put("message", "文件不存在");
        }

        return ResponseEntity.ok(response);
    }
}

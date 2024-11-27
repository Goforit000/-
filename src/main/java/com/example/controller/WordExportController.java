package com.example.controller;

import com.example.service.WordExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WordExportController {

    @Autowired
    private WordExportService wordExportService;

    // 处理导出请求
    @GetMapping("/api/exportWord")
    public ResponseEntity<byte[]> exportWord(@RequestParam("bigProjectId") int bigProjectId) {
        try {
            // 调用 Service 生成 Word 文件
            byte[] wordFile = wordExportService.generateWordFile(bigProjectId);

            // 设置响应头，告知浏览器下载为 Word 文件
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=exported_file.docx");
            headers.add("Content-Type", "application/msword");

            // 返回文件内容，带上文件名
            return new ResponseEntity<>(wordFile, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

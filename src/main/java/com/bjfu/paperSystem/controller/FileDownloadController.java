package com.bjfu.paperSystem.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/download")
public class FileDownloadController {

    /**
     * 下载新闻附件
     */
    @GetMapping("/news")
    public ResponseEntity<Resource> downloadNewsAttachment(@RequestParam String path, @RequestParam(required = false) String filename) {
        try {
            // 将路径转换为实际文件路径
            // path格式: /uploads/news_attachments/filename
            // 需要去掉 /uploads/ 前缀，因为文件实际存储在 D:/java_uploads/news_attachments/filename
            String normalizedPath = path;
            if (normalizedPath.startsWith("/uploads/")) {
                normalizedPath = normalizedPath.substring("/uploads/".length());
            } else if (normalizedPath.startsWith("uploads/")) {
                normalizedPath = normalizedPath.substring("uploads/".length());
            }
            String filePath = "D:/java_uploads/" + normalizedPath;
            File file = new File(filePath);
            
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // 设置文件名
            String encodedFilename = filename != null ? 
                URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()) : 
                URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.toString());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename)
                    .body(resource);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}


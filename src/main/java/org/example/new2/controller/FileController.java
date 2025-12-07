package org.example.new2.controller;
import org.example.new2.common.Result;
import org.example.new2.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/file")
public class FileController {
    @Value("${file.upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = FileUtil.upload(file, uploadPath);
            // 返回可访问的 URL 路径 (前端拿到这个存入 avatar 字段)
            // 假设我们映射了 /images/** 到本地目录
            return Result.success("/images/" + fileName);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}

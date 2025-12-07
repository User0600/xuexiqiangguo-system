package org.example.new2.util;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUtil {

    /**
     * 上传文件
     * @param file 前端传来的文件
     * @param uploadPath 服务器存储路径
     * @return 存储后的文件名 (用于存数据库)
     */
    public static String upload(MultipartFile file, String uploadPath) throws IOException {
        // 1. 获取原始文件名
        String originalFilename = file.getOriginalFilename();

        // 2. 获取后缀名 (如 .jpg)
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 3. 安全检查 (第3种安全设计：防止上传 .exe .sh 等恶意脚本)
        if (!isValidImage(suffix)) {
            throw new RuntimeException("禁止上传非图片格式的文件！");
        }

        // 4. 生成新文件名 (UUID防止重名)
        String newFileName = UUID.randomUUID().toString() + suffix;

        // 5. 创建目录
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 6. 保存文件
        File dest = new File(uploadPath + newFileName);
        file.transferTo(dest);

        return newFileName;
    }

    private static boolean isValidImage(String suffix) {
        String s = suffix.toLowerCase();
        return s.equals(".jpg") || s.equals(".jpeg") || s.equals(".png") || s.equals(".gif");
    }


}

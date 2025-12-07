package org.example.new2.dto;
import lombok.Data;
@Data
public class UserProfileDTO {
    // 允许修改的字段
    private String username; // 昵称
    private String phone;    // 手机号
    private String email;    // 邮箱
    private String avatar;   // 头像路径
}

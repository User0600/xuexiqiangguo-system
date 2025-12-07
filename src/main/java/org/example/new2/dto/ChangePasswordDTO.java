package org.example.new2.dto;
import lombok.Data;
@Data
public class ChangePasswordDTO {
    private String oldPassword; // 旧密码
    private String newPassword; // 新密码
}

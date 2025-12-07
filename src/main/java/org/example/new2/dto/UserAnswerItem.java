package org.example.new2.dto;
import lombok.Data;
@Data
public class UserAnswerItem {
    private Long questionId;
    private String answer; // 用户选的答案 "A"
}

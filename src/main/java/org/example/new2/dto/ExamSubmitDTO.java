package org.example.new2.dto;
import lombok.Data;
import java.util.List;

@Data
public class ExamSubmitDTO {
    private Long taskId;
    private Integer category; // 当前提交的是哪个板块 (1/2/3)
    private List<UserAnswerItem> answers;
}

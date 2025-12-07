package org.example.new2.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;
@Data
public class QuestionDTO {
    private Long id; // 修改时需要
    private String content;
    private Integer type;
    private Integer category;
    private List<Map<String, Object>> options; // JSON 数组
    private String correctAnswer;
    private String analysis;
    private Long creatorId; // 实际项目中通常从 Token 获取，这里模拟传参
    private String creatorName;
}

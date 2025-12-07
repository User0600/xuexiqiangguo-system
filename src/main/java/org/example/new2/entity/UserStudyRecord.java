package org.example.new2.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@TableName("user_study_record")
public class UserStudyRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long taskId;

    // 分数：Null代表未考，>=0代表已考
    private Integer scoreNews;     // 1
    private Integer scorePro;      // 2
    private Integer scorePolitics; // 3

    private Integer totalScore;
    private Integer status; // 0-进行中, 1-已完成
    private LocalDateTime finishTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

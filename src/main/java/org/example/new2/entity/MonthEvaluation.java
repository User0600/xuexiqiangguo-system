package org.example.new2.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("month_evaluation")

public class MonthEvaluation implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long userId;
    private String monthStr;
    private Integer totalScore;
    private String rankLevel;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

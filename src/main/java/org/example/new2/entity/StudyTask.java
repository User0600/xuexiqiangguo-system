package org.example.new2.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@TableName("study_task")
public class StudyTask {
    @TableId(type=IdType.AUTO)
    private Long id;
    private String title;
    private Integer weekNum;
    private Integer status;

    @TableField(fill =FieldFill.INSERT)
    private LocalDateTime createTime;

}

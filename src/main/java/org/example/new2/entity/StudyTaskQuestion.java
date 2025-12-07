package org.example.new2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
@Data
@TableName("study_task_question")
public class StudyTaskQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private Long questionId;
    private Integer category; // 1-时事, 2-专业, 3-政治

}

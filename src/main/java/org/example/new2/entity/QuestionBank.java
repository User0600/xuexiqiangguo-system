package org.example.new2.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 题库表实体
 * 必须开启 autoResultMap = true 才能让 JSON 处理器生效
 */
@Data
@TableName(value = "question_bank", autoResultMap = true)
public class QuestionBank implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 题目题干内容 */
    private String content;

    /** 题目类型: 1-单选, 2-多选 */
    private Integer type;

    /** 分类: 1-时事, 2-专业, 3-政治 */
    private Integer category;

    /**
     * 选项内容(JSON格式)
     * 使用 MyBatis-Plus 自带的 JacksonTypeHandler 自动转换 JSON 和 List
     * 前端传入/传出格式：[{"label":"A", "text":"..."}, {"label":"B", "text":"..."}]
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> options;

    /** 正确答案，如 "A" 或 "AB" */
    private String correctAnswer;

    /** 题目解析 */
    private String analysis;

    /** 提交人ID */
    private Long creatorId;

    /** 提交人姓名 */
    private String creatorName;

    /** 状态: 0-待审核, 1-已入库, 2-驳回 */
    private Integer status;

    /** 逻辑删除: 0-未删, 1-已删 */
    @TableLogic
    private Integer isDeleted;

}

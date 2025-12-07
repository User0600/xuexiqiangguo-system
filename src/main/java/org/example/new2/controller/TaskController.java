package org.example.new2.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.new2.common.Result;
import org.example.new2.dto.ExamSubmitDTO;
import org.example.new2.entity.*;
import org.example.new2.service.*;
import org.example.new2.mapper.StudyTaskQuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/task")
public class TaskController {
    @Autowired private TaskService taskService;
    @Autowired private ExamService examService;
    @Autowired private StudyTaskQuestionMapper studyTaskQuestionMapper;
    @Autowired private QuestionService questionService;

    // ================== 管理员端 ==================

    /**
     * 1. 发布本周学习任务 (自动抽15道题)
     */
    @PostMapping("/generate")
    public Result<String> generate(@RequestParam String title, @RequestParam Integer weekNum) {
        taskService.generateWeeklyTask(title, weekNum);
        return Result.success("本周任务发布成功，已生成15道题目");
    }

    // ================== 用户端 ==================

    /**
     * 2. 首页：获取最新任务及我的完成进度
     * 用户登录后，调用此接口展示三个板块的状态
     */
    @GetMapping("/current")
    public Result<UserStudyRecord> getCurrentTaskInfo(@RequestHeader(value="userId", defaultValue = "1") Long userId) {
        // 1. 找最新发布的一个任务
        StudyTask task = taskService.getOne(new LambdaQueryWrapper<StudyTask>()
                .eq(StudyTask::getStatus, 1)
                .orderByDesc(StudyTask::getId)
                .last("LIMIT 1"));

        if (task == null) return Result.error("当前没有学习任务");

        // 2. 找用户的学习记录
        UserStudyRecord record = examService.getOne(new LambdaQueryWrapper<UserStudyRecord>()
                .eq(UserStudyRecord::getUserId, userId)
                .eq(UserStudyRecord::getTaskId, task.getId()));

        // 如果没记录，返回一个空对象给前端，前端判断 scoreXXX 为 null 显示“去答题”
        if (record == null) {
            record = new UserStudyRecord();
            record.setTaskId(task.getId());
            // 前端根据 scoreNews == null 来判断是否已考
            record.setUserId(userId);
        }

        return Result.success(record);
    }

    /**
     * 3. 进入板块：获取该板块的 5 道题
     * @param taskId 任务ID
     * @param category 1-时事, 2-专业, 3-政治
     */
    @GetMapping("/questions")
    public Result<List<QuestionBank>> getQuestions(@RequestParam Long taskId, @RequestParam Integer category) {
        // 1. 在关联表中查出这个任务、这个分类下的5个题目ID
        List<StudyTaskQuestion> relations = studyTaskQuestionMapper.selectList(
                new LambdaQueryWrapper<StudyTaskQuestion>()
                        .eq(StudyTaskQuestion::getTaskId, taskId)
                        .eq(StudyTaskQuestion::getCategory, category)
        );

        if (relations.isEmpty()) return Result.error("该板块题目数据缺失");

        List<Long> qIds = relations.stream().map(StudyTaskQuestion::getQuestionId).collect(Collectors.toList());

        // 2. 查题目详情 (记得把 correct_answer 抹除，防止作弊，这里为了简单先不抹)
        List<QuestionBank> questions = questionService.listByIds(qIds);

        // 生产环境建议：questions.forEach(q -> q.setCorrectAnswer(null));
        return Result.success(questions);
    }

    /**
     * 4. 提交板块：用户交卷
     */
    @PostMapping("/submit")
    public Result<Integer> submitSection(@RequestBody ExamSubmitDTO dto,
                                         @RequestHeader(value="userId", defaultValue = "1") Long userId) {
        // 模拟从 Token 获取 userId，这里暂时用 header 传
        Integer score = examService.submitSection(userId, dto);
        return Result.success(score);
    }
}

package org.example.new2.service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.new2.entity.*;
import org.example.new2.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service

public class TaskService extends ServiceImpl<StudyTaskMapper,StudyTask> {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private StudyTaskQuestionMapper taskQuestionMapper;

    /**
     * 生成本周任务：三个板块各抽5道题
     */
    @Transactional
    public void generateWeeklyTask(String title, Integer weekNum) {
        // 1. 创建任务主表
        StudyTask task = new StudyTask();
        task.setTitle(title);
        task.setWeekNum(weekNum);
        task.setStatus(1); // 直接发布
        this.save(task);

        List<StudyTaskQuestion> relations = new ArrayList<>();

        // 2. 分别抽取 1-时事, 2-专业, 3-政治
        for (int category = 1; category <= 3; category++) {
            // 每个分类抽 5 题
            List<QuestionBank> questions = questionMapper.selectRandomByCategory(category, 5);

            for (QuestionBank q : questions) {
                StudyTaskQuestion rel = new StudyTaskQuestion();
                rel.setTaskId(task.getId());
                rel.setQuestionId(q.getId());
                rel.setCategory(category); // 记录这道题属于哪个板块
                relations.add(rel);
            }
        }

        // 3. 批量保存关联关系 (MyBatis-Plus ServiceImpl 自带方法)
        // 注意：这里需要单独为 StudyTaskQuestion 建一个 Service，或者直接用 Mapper 循环插
        // 为了演示方便，这里模拟批量插入 (实际建议注入 StudyTaskQuestionService 使用 saveBatch)
        for(StudyTaskQuestion rel : relations){
            taskQuestionMapper.insert(rel);
        }
    }
}

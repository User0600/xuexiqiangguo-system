package org.example.new2.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.new2.dto.ExamSubmitDTO;
import org.example.new2.dto.UserAnswerItem;
import org.example.new2.entity.QuestionBank;
import org.example.new2.entity.UserStudyRecord;
import org.example.new2.mapper.QuestionMapper;
import org.example.new2.mapper.UserStudyRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class ExamService extends ServiceImpl<UserStudyRecordMapper,UserStudyRecord> {
    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 提交某一板块的试卷
     * @param userId 当前用户
     * @param dto 提交的数据
     * @return 本次得分
     */
    @Transactional
    public Integer submitSection(Long userId, ExamSubmitDTO dto) {
        // 1. 获取题目ID列表
        List<Long> questionIds = dto.getAnswers().stream()
                .map(UserAnswerItem::getQuestionId).collect(Collectors.toList());

        // 2. 查出标准答案
        List<QuestionBank> questions = questionMapper.selectBatchIds(questionIds);
        Map<Long, String> correctMap = questions.stream()
                .collect(Collectors.toMap(QuestionBank::getId, QuestionBank::getCorrectAnswer));

        // 3. 计算分数 (每题20分，共100分)
        int currentScore = 0;
        for (UserAnswerItem item : dto.getAnswers()) {
            String right = correctMap.get(item.getQuestionId());
            if (right != null && right.equalsIgnoreCase(item.getAnswer())) {
                currentScore += 20;
            }
        }

        // 4. 获取或创建用户的学习记录
        UserStudyRecord record = this.getOne(new LambdaQueryWrapper<UserStudyRecord>()
                .eq(UserStudyRecord::getUserId, userId)
                .eq(UserStudyRecord::getTaskId, dto.getTaskId()));

        if (record == null) {
            record = new UserStudyRecord();
            record.setUserId(userId);
            record.setTaskId(dto.getTaskId());
            record.setStatus(0);
        }

        // 5. 根据分类更新对应字段
        int category = dto.getCategory(); // 1-时事, 2-专业, 3-政治
        if (category == 1) record.setScoreNews(currentScore);
        else if (category == 2) record.setScorePro(currentScore);
        else if (category == 3) record.setScorePolitics(currentScore);

        // 6. 检查是否全部完成 (三个分数都不为 null)
        if (record.getScoreNews() != null &&
                record.getScorePro() != null &&
                record.getScorePolitics() != null) {

            record.setStatus(1); // 标记完成
            record.setFinishTime(LocalDateTime.now());
            // 计算总分
            record.setTotalScore(record.getScoreNews() + record.getScorePro() + record.getScorePolitics());
        } else {
            // 实时更新总分显示进度
            int s1 = record.getScoreNews() == null ? 0 : record.getScoreNews();
            int s2 = record.getScorePro() == null ? 0 : record.getScorePro();
            int s3 = record.getScorePolitics() == null ? 0 : record.getScorePolitics();
            record.setTotalScore(s1 + s2 + s3);
        }

        this.saveOrUpdate(record);
        return currentScore;
    }

}

package org.example.new2.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.new2.entity.MonthEvaluation;
import org.example.new2.mapper.MonthEvaluationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
@Component
public class MonthEvaluationTask {
    @Autowired
    private MonthEvaluationMapper monthMapper;

    /**
     * 每月最后一天 23:30 执行自动统计
     */
    @Scheduled(cron = "0 30 23 L * ?")
    @Transactional
    public void executeMonthlyJob() {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        System.out.println(">>> 开始执行月度考核统计: " + currentMonth);

        // 1. 算出每人总分
        List<Map<String, Object>> scores = monthMapper.selectMonthTotalScore(currentMonth);
        if(scores == null || scores.isEmpty()) return;

        // 2. 评级并入库
        for (Map<String, Object> s : scores) {
            Long userId = (Long) s.get("userId");
            Number totalNum = (Number) s.get("monthTotal");
            int total = totalNum == null ? 0 : totalNum.intValue();

            String rank = total >= 400 ? "A" : (total >= 200 ? "B" : "C");

            MonthEvaluation ev = new MonthEvaluation();
            ev.setUserId(userId);
            ev.setMonthStr(currentMonth);
            ev.setTotalScore(total);
            ev.setRankLevel(rank);

            // 如果已存在则更新，不存在则插入
            MonthEvaluation exist = monthMapper.selectOne(new LambdaQueryWrapper<MonthEvaluation>()
                    .eq(MonthEvaluation::getUserId, userId)
                    .eq(MonthEvaluation::getMonthStr, currentMonth));

            if(exist != null) {
                ev.setId(exist.getId());
                monthMapper.updateById(ev);
            } else {
                monthMapper.insert(ev);
            }
        }
        System.out.println("<<< 月度考核统计结束");
    }
}

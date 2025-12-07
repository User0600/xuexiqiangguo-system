package org.example.new2.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.new2.entity.MonthEvaluation;

import java.util.List;
import java.util.Map;
@Mapper
public interface MonthEvaluationMapper extends BaseMapper<MonthEvaluation> {
    /**
     * 自定义统计 SQL：
     * 查询指定月份(monthStr)下，所有用户的学习记录总分。
     * 逻辑：关联 user_study_record 和 study_task 表，筛选出该月发布的任务，并按用户汇总分数。
     *
     * @param monthStr 月份，如 "2023-12"
     * @return 列表，包含 {user_id=1, total=500}
     */
    /**
     * 统计指定月份每个人的总分
     * 逻辑：把本月所有任务的分数加起来
     */
    @Select("SELECT r.user_id as userId, SUM(r.total_score) as monthTotal " +
            "FROM user_study_record r " +
            "LEFT JOIN study_task t ON r.task_id = t.id " +
            "WHERE DATE_FORMAT(t.create_time, '%Y-%m') = #{monthStr} " +
            "AND r.status = 1 " +
            "GROUP BY r.user_id")
    List<Map<String, Object>> selectMonthTotalScore(@Param("monthStr") String monthStr);

}

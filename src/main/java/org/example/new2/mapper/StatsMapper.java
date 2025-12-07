package org.example.new2.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.new2.vo.WeeklyExportVO;
import java.util.List;

@Mapper
public interface StatsMapper {

    /**
     * 核心报表查询 SQL
     * 1. 使用 LEFT JOIN `user` (加反引号) 关联用户表
     * 2. 使用 u.id as userId 对应 VO 中的 userId 字段
     * 3. 使用 u.username as username 对应 VO 中的 username 字段
     */
    @Select("SELECT " +
            "u.id as userId, " +           // 对应 WeeklyExportVO.userId
            "u.username as username, " +   // 对应 WeeklyExportVO.username
            "t.title as taskTitle, " +
            "r.score_news as scoreNews, " +
            "r.score_pro as scorePro, " +
            "r.score_politics as scorePolitics, " +
            "r.total_score as totalScore, " +
            "r.status as rawStatus, " +
            "r.finish_time as finishTime " +
            "FROM user_study_record r " +
            "LEFT JOIN `user` u ON r.user_id = u.id " +  // ✅ 必须加反引号
            "LEFT JOIN study_task t ON r.task_id = t.id " +
            "WHERE t.id = #{taskId} " +
            "ORDER BY r.total_score DESC")
    List<WeeklyExportVO> selectWeeklyReport(@Param("taskId") Long taskId);
}

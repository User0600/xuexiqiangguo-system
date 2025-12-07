package org.example.new2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.new2.entity.QuestionBank;
import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<QuestionBank> {
    // 继承 BaseMapper 后，自动拥有 CRUD 能力，无需手写 XML
    // 核心 SQL：根据分类，随机抽取 N 条已入库(status=1)的题目
    @Select("SELECT * FROM question_bank WHERE category = #{category} AND status = 1 ORDER BY RAND() LIMIT #{limit}")
    List<QuestionBank> selectRandomByCategory(@Param("category") Integer category, @Param("limit") Integer limit);

}

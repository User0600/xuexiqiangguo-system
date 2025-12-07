package org.example.new2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.new2.entity.Admin;
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    // MP 内置了 selectOne, selectById 等，无需手动写 findByUsername
}

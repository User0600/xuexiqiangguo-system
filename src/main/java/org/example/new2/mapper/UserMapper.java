package org.example.new2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.new2.entity.User;
@Mapper
public interface UserMapper  extends BaseMapper<User> {
    // 模糊查询等复杂操作在 Service 层通过 Wrapper 组装，无需在此定义

}

package com.staticanalyzer.staticanalyzer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.staticanalyzer.staticanalyzer.entity.user.User;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

package com.staticanalyzer.staticanalyzer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.staticanalyzer.staticanalyzer.entity.user.User;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户映射
 * 
 * @author WLLEGit
 * @since 0.0.1
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

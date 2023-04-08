package com.staticanalyzer.staticanalyzer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.staticanalyzer.staticanalyzer.entities.User;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Many;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE id=#{id}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "id", property = "projectIdList", javaType = List.class, many = @Many(select = "com.staticanalyzer.staticanalyzer.mapper.ProjectMapper.selectIdByUserId"))
    })
    User selectWithProjectIdById(@Param("id") int id);
}

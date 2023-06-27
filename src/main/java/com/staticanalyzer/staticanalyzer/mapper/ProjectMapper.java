package com.staticanalyzer.staticanalyzer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.staticanalyzer.staticanalyzer.entity.project.Project;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    @Select("SELECT * FROM project WHERE user_id=#{userId}")
    java.util.List<Project> selectByUserId(@Param("userId") int userId);

}

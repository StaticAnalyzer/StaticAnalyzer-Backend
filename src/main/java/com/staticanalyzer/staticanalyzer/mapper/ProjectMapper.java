package com.staticanalyzer.staticanalyzer.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.staticanalyzer.staticanalyzer.entity.project.Project;

/**
 * 项目映射
 * 
 * @author WLLEGit
 * @since 0.0.1
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 通过所有者id获取项目列表
     * 
     * @param userId
     * @return 项目列表
     */
    @Select("SELECT * FROM project WHERE user_id=#{userId}")
    List<Project> selectByUserId(@Param("userId") int userId);
}

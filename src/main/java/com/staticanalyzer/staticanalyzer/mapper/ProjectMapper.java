package com.staticanalyzer.staticanalyzer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.staticanalyzer.staticanalyzer.entity.ProjectDO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProjectMapper extends BaseMapper<ProjectDO> {
    @Select("SELECT * FROM project WHERE user_id=#{userId}")
    List<ProjectDO> selectByUserId(@Param("userId") int userId);
}

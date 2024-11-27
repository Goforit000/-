package com.example.mapper;


import com.example.entity.Subsystem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SubsystemMapper {

    // 删除子系统
    @Delete("DELETE FROM subsystem WHERE subsystem_id = #{subsystemId} AND project_id = #{projectId}")
    int delete(@Param("subsystemId") Integer subsystemId, @Param("projectId") Integer projectId);


    // 根据子系统ID和项目ID查询子系统
    @Select("SELECT subsystem_id, subsystem_name, subsystem_describe, unadjusted_fp, adjusted_fp, adjusted_method, project_id " +
            "FROM subsystem WHERE subsystem_id = #{subsystemId} AND project_id = #{projectId}")
    @Results({
            @Result(property = "subsystemId", column = "subsystem_id"),
            @Result(property = "subsystemName", column = "subsystem_name"),
            @Result(property = "subsystemDescribe", column = "subsystem_describe"),
            @Result(property = "unadjustedFp", column = "unadjusted_fp"),
            @Result(property = "adjustedFp", column = "adjusted_fp"),
            @Result(property = "adjustedMethod", column = "adjusted_method"),
            @Result(property = "projectId", column = "project_id")
    })
    Subsystem selectById(@Param("subsystemId") Integer subsystemId, @Param("projectId") Integer projectId);

    // 根据项目ID查询子系统
    @Select("SELECT * FROM subsystem WHERE project_id = #{projectId}")
    @Results({
            @Result(property = "subsystemId", column = "subsystem_id"),
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "subsystemName", column = "subsystem_name"),
            @Result(property = "subsystemDescribe", column = "subsystem_describe"),
            @Result(property = "unadjustedFp", column = "unadjusted_fp"),
            @Result(property = "adjustedFp", column = "adjusted_fp"),
            @Result(property = "adjustedMethod", column = "adjusted_method")
    })
    List<Subsystem> selectByProjectId(@Param("projectId") Integer projectId);


    // 检查子系统是否已经存在
    @Select("SELECT COUNT(1) FROM subsystem WHERE subsystem_id = #{subsystemId} AND project_id = #{projectId}")
    boolean existsSubsystem(@Param("subsystemId") Integer subsystemId, @Param("projectId") Integer projectId);

    // 更新已有子系统
    @Update({
            "<script>",
            "UPDATE subsystem SET",
            "subsystem_name = #{subsystemName},",
            "subsystem_describe = #{subsystemDescribe},",
            "unadjusted_fp = #{unadjustedFp},",
            "adjusted_fp = #{adjustedFp},",
            "adjusted_method = #{adjustedMethod}",
            "WHERE subsystem_id = #{subsystemId} AND project_id = #{projectId}",
            "</script>"
    })
    int updateSubsystem(Subsystem subsystem);

    // 插入新子系统
      // 插入 subsystem 数据
    @Insert({
    "INSERT INTO subsystem (subsystem_name, subsystem_describe, unadjusted_fp, adjusted_fp, adjusted_method, project_id) ",
    "VALUES (#{subsystemName}, #{subsystemDescribe}, #{unadjustedFp}, #{adjustedFp}, #{adjustedMethod}, #{projectId})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "subsystemId", keyColumn = "subsystem_id")  // 获取生成的 subsystemId
    int insertSubsystem(Subsystem subsystem);


    // 插入 cost_evaluation 数据
    @Insert({
        "INSERT INTO cost_evaluation (project_id, project_name,big_project_id) ",
        "VALUES (#{subsystemId},#{subsystemName}, #{projectId})"
    })
    int insertCostEvaluation(Subsystem subsystem);



}

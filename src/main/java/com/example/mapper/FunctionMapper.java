package com.example.mapper;


import com.example.entity.FunctionPoint;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FunctionMapper {

    // 根据子系统ID查询功能点
    @Select("SELECT subsystem_id AS subsystemId, project_id AS projectId, feature_id AS featureId, feature_name AS featureName, " +
            "complexity, modify_type AS modifyType, feature_type AS featureType, note " +
            "FROM function_points " +
            "WHERE subsystem_id = #{subsystemId}")
    List<FunctionPoint> findFunctionPointsBySubsystemId(@Param("subsystemId") Integer subsystemId);


        // 根据项目ID和子系统ID查询功能点
        @Select("SELECT * FROM function_points WHERE project_id = #{projectId} AND subsystem_id = #{subsystemId}")
        @Results({
                @Result(property = "featureId", column = "feature_id"),
                @Result(property = "featureName", column = "feature_name"),
                @Result(property = "complexity", column = "complexity"),
                @Result(property = "modifyType", column = "modify_type"),
                @Result(property = "featureType", column = "feature_type"),
                @Result(property = "note", column = "note"),
                @Result(property = "subsystemId", column = "subsystem_id"),
                @Result(property = "projectId", column = "project_id")
        })
        List<FunctionPoint> selectByProjectIdAndSubsystemId(@Param("projectId") Integer projectId,
                                                            @Param("subsystemId") Integer subsystemId);

        // 检查功能点是否已经存在
        @Select("SELECT COUNT(1) FROM function_points WHERE feature_id = #{featureId} AND project_id = #{projectId} AND subsystem_id = #{subsystemId}")
        boolean existsFunctionPoint(@Param("featureId") Integer featureId,
                                    @Param("projectId") Integer projectId,
                                    @Param("subsystemId") Integer subsystemId);

        // 根据功能点ID查询功能点
        @Select("SELECT * FROM function_points WHERE feature_id = #{featureId} AND project_id = #{projectId} AND subsystem_id = #{subsystemId}")
        FunctionPoint selectByFeatureId(@Param("featureId") Integer featureId,
                                        @Param("projectId") Integer projectId,
                                        @Param("subsystemId") Integer subsystemId);

        // 更新功能点
        @Update({
                "<script>",
                "UPDATE function_points SET",
                "feature_name = #{featureName},",
                "complexity = #{complexity},",
                "modify_type = #{modifyType},",
                "feature_type = #{featureType},",
                "note = #{note}",
                "WHERE feature_id = #{featureId} AND project_id = #{projectId} AND subsystem_id = #{subsystemId}",
                "</script>"
        })
        int updateFunctionPoint(FunctionPoint functionPoint);

        // 插入功能点
        @Insert({
                "<script>",
                "INSERT INTO function_points (feature_name, complexity, modify_type, feature_type, note, feature_id, subsystem_id, project_id) VALUES",
                "(#{featureName}, #{complexity}, #{modifyType}, #{featureType}, #{note}, #{featureId}, #{subsystemId}, #{projectId})",
                "</script>"
        })
        int insertFunctionPoint(FunctionPoint functionPoint);


    // 删除功能点
    @Delete("DELETE FROM function_points WHERE feature_id = #{featureId} AND subsystem_id = #{subsystemId}")
    int deleteFunctionPoint(@Param("featureId") Integer featureId, @Param("subsystemId") Integer subsystemId);


}

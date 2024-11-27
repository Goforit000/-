package com.example.mapper;



import com.example.entity.Detail;
import com.example.entity.Subsystem;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface DetailFunctionMapper {

 /**
     * 根据项目 ID 和子系统 ID 查询功能点及相关信息
     *
     * @param projectId   项目 ID
     * @return 查询结果列表
     */
    @Select("""
               SELECT 
            s.project_id,
            f.feature_name,
            f.complexity,
            f.modify_type,
            f.feature_type,
            f.note,
            s.subsystem_id,
            s.subsystem_name,
            s.subsystem_describe,
            s.unadjusted_fp,
            s.adjusted_fp
        FROM 
            function_points f
        JOIN 
            subsystem s
        ON 
            f.subsystem_id = s.subsystem_id AND f.project_id = s.project_id
        WHERE 
            s.project_id = #{projectId}
        """)
   @Results({
    @Result(property = "projectId", column = "project_id"),         // 子系统 ID
    @Result(property = "subsystemName", column = "subsystem_name"),     // 子系统名称
    @Result(property = "subsystemDescribe", column = "subsystem_describe"), // 子系统描述
    @Result(property = "unadjustedFp", column = "unadjusted_fp"),       // 调整前功能点
    @Result(property = "adjustedFp", column = "adjusted_fp"),           // 调整后功能点
    @Result(property = "featureName", column = "feature_name"),         // 功能点名称
    @Result(property = "complexity", column = "complexity"),            // 复杂度
    @Result(property = "modifyType", column = "modify_type"),           // 修改类型
    @Result(property = "featureType", column = "feature_type"),         // 功能类型
    @Result(property = "note", column = "note")                         // 备注
})
    List<Detail> findAllDetails(@Param("projectId") int projectId);



    @Select(" SELECT total_unadjusted_fp FROM project Where project_id = #{projectId}")
    @Results({
            @Result(property = "totalUnadjustedFp", column = "total_unadjusted_fp")
    })
    double findPrePoint(@Param("projectId") int projectId);

    @Select(" SELECT total_adjusted_fp FROM project Where project_id = #{projectId}")
    @Results({
            @Result(property = "totalAdjustedFp", column = "total_adjusted_fp")
    })
    double findAdjPoint(@Param("projectId") int projectId);

    @Update("UPDATE project SET total_unadjusted_fp = #{totalUnadjustedFp} WHERE project_id = #{projectId}")
    void updateTotalUnadjustedFp(@Param("projectId") int projectId, @Param("totalUnadjustedFp") double totalUnadjustedFp);


    @Update("UPDATE project SET total_adjusted_fp = #{totalAdjustedFp} WHERE project_id = #{projectId}")
    void updateTotalAdjustedFp(@Param("projectId") int projectId, @Param("totalAdjustedFp") double totalAdjustedFp);

    @Insert("""
        INSERT INTO subsystem 
        (project_id, subsystem_name, subsystem_describe, unadjusted_fp, adjusted_fp, adjusted_method)
        VALUES
        (#{projectId}, #{subsystem.subsystemName}, #{subsystem.subsystemDescribe}, 
        #{subsystem.unadjustedFp}, #{subsystem.adjustedFp}, #{subsystem.adjustedMethod})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "subsystem.subsystemId")
    void insertSubsystem(@Param("projectId") int projectId, @Param("subsystem") Subsystem subsystem);

    @Insert("""
    INSERT INTO cost_evaluation 
    (project_id, project_name, big_project_id, adjustedFP)
    VALUES (#{subsystem.subsystemId}, #{subsystem.subsystemName}, #{subsystem.projectId}, #{subsystem.adjustedFp})
""")
    void insertCostEvaluation(@Param("projectId") int projectId, @Param("subsystem") Subsystem subsystem);


    @Insert("""
        INSERT INTO function_points 
        (subsystem_id,  feature_name, complexity, modify_type, feature_type, note, project_id)
        VALUES
        (#{subsystemId}, #{featureName}, 
        #{complexity}, #{modifyType}, #{featureType}, 
        #{note}, #{projectId})
    """)
    void insertFunctionPoint(@Param("subsystemId") int subsystemId,
                             @Param("featureName") String featureName, @Param("complexity") String complexity,
                             @Param("modifyType") String modifyType, @Param("featureType") String featureType,
                             @Param("note") String note, @Param("projectId") int projectId);



}

package com.example.mapper;



import com.example.entity.Project;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProjectMapper {
        @Insert(
            "INSERT INTO project(project_name,note) VALUES (#{projectName},#{note})"
        )
        @Results({
            @Result(property = "note", column = "note"),
            @Result(property ="projectName",column = "project_name")
        })
          int insertProject(Project project);

    //3
        @Select("SELECT * FROM project")
            @Results({
            @Result(property = "note", column = "note"),
            @Result(property ="projectName",column = "project_name"),
            @Result(property ="projectId",column = "project_id"),
            @Result(property ="totalAdjustedFp",column = "total_adjusted_fp"),
            @Result(property = "totalUnadjustedFp", column ="total-unadjusted_fp"),
            })
        List<Project> selectProjectsThird();

    @Select("<script>" +
            "SELECT * FROM project " +
            "<where>" +
            "  <if test='name != null and name != \"\"'> AND project_name LIKE CONCAT('%', #{name}, '%') </if>" +
            "  <if test='status != null and status != \"\" and status != \"all\"'> AND status = #{status} </if>" +
            "</where>" +
            "LIMIT #{size} OFFSET #{offset}" +
            "</script>")
    @Results({
            @Result(property = "note", column = "note"),
            @Result(property = "projectName", column = "project_name"),
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "totalAdjustedFp", column = "total_adjusted_fp"),
            @Result(property = "totalUnadjustedFp", column = "total_unadjusted_fp"),
    })
    List<Project> selectProjects(@Param("offset") int offset, @Param("size") int size,
                                 @Param("name") String name, @Param("status") String status);

    @Select("<script>" +
            "SELECT COUNT(*) FROM project " +
            "<where>" +
            "  <if test='name != null and name != \"\"'> AND project_name LIKE CONCAT('%', #{name}, '%') </if>" +
            "  <if test='status != null and status != \"\" and status != \"all\"'> AND status = #{status} </if>" +
            "</where>" +
            "</script>")
    int selectTotalCount(@Param("name") String name, @Param("status") String status);



@Delete("delete from project where project_id=#{projectId}")
    @Result(property ="projectId",column = "project_id")
    int deleteProjectById(int projectId);

@Update("UPDATE project SET project_name=#{projectName}, note=#{note} WHERE project_id=#{projectId}")
    @Results({
            @Result(property ="projectName",column = "project_name"),
    @Result(property ="projectId",column = "project_id")
    })
    int updateProject(Project project);
 @Select("SELECT * FROM project WHERE project_name  LIKE CONCAT('%',#{projectName},'%') ")
     @Result(property ="projectName",column = "project_name")
    List<Project> searchProjects(String projectName);
}

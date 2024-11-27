package com.example.mapper;

import com.example.entity.CostEvaluation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CostEvaluationMapper {
    //将上个表数据加入到cost_evaluation表‘
//     @Insert("INSERT INTO cost_evaluation(big_project_id, project_id, project_name) " +
//            "SELECT project_id AS big_project_id, subsystem_id AS project_id, subsystem_name AS project_name " +
//            "FROM subsystem")
//    void insert();
//
//    @Select("SELECT project_id, project_name, adjustedFP " +
//            "FROM cost_evaluation " +
//            "WHERE big_project_id = #{bigProjectId}")
//    List<CostEvaluation> selectByBigProjectId(int bigProjectId);
    @Select("SELECT ce.project_id, ce.project_name, ss.adjusted_fp, ce.big_project_id " +
        "FROM cost_evaluation ce " +
        "JOIN subsystem ss ON ce.project_id = ss.subsystem_id " +  // 修正了连接条件
        "WHERE ce.big_project_id = #{bigProjectId}")
    List<CostEvaluation> selectByBigProjectId(int bigProjectId);

     @Select("SELECT "
            + "ce.project_id, ce.project_name, ce.adjustedFP, ce.upper_PDR, "
            + "ce.mid_PDR, ce.floor_PDR, ce.business_domain, ce.application_type, "
            + "ce.quality_characteristic, ce.develop_language, ce.team_background, "
            + "ce.upper_workload, ce.mid_workload, ce.floor_workload, ce.risk, "
            + "ce.upper_cost, ce.mid_cost, ce.floor_cost, ce.upper_adjusted_cost, "
            + "ce.mid_adjusted_cost, ce.floor_adjusted_cost, ce.direct_nonhuman_cost, "
            + "ce.straight_quality_grade, ce.labor_cost_rate, ce.big_project_id, "
            + "ss.subsystem_id, ss.subsystem_name, ss.subsystem_describe, "
            + "ss.unadjusted_fp, ss.adjusted_fp, ss.adjusted_method "
            + "FROM cost_evaluation ce "
            + "JOIN subsystem ss ON ce.big_project_id = ss.project_id AND ce.project_id = ss.subsystem_id "
            + "WHERE ce.big_project_id = #{bigProjectId}")
    List<CostEvaluation> selectSubsystemByBigProjectId(int bigProjectId);
    @Select("SELECT "
         + "ce.project_id, "
         + "ce.project_name, "
         + "ce.adjustedFP, "
         + "ce.upper_PDR, "
         + "ce.mid_PDR, "
         + "ce.floor_PDR, "
         + "ce.business_domain, "
         + "ce.application_type, "
         + "ce.quality_characteristic, "
         + "ce.develop_language, "
         + "ce.team_background, "
         + "ce.upper_workload, "
         + "ce.mid_workload, "
         + "ce.floor_workload, "
         + "ce.risk, "
         + "ce.upper_cost, "
         + "ce.mid_cost, "
         + "ce.floor_cost, "
         + "ce.upper_adjusted_cost, "
         + "ce.mid_adjusted_cost, "
         + "ce.floor_adjusted_cost, "
         + "ce.direct_nonhuman_cost, "
         + "ce.straight_quality_grade, "
         + "ce.labor_cost_rate, "
            +"ce.big_project_id, "
         + "ss.subsystem_id, "
         + "ss.subsystem_name, "
         + "ss.subsystem_describe, "
         + "ss.unadjusted_fp, "
         + "ss.adjusted_fp, "
         + "ss.adjusted_method "
         + "FROM cost_evaluation ce "
         + "JOIN subsystem ss ON ce.big_project_id = ss.project_id AND ce.project_id=ss.subsystem_id "
         + "WHERE ce.project_id = #{projectId}")
List<CostEvaluation> selectSubsystemById(int projectId);


    @Select("SELECT * FROM cost_evaluation WHERE project_id = #{projectId}")
    CostEvaluation selectById(int projectId);

    //获取所有
    @Select("SELECT * FROM cost_evaluation")
    List<CostEvaluation> selectAll();

    @Insert("INSERT INTO cost_evaluation (project_id, project_name, adjustedFP, upper_PDR, mid_PDR, floor_PDR, business_domain, " +
            "application_type, quality_characteristic, develop_language, team_background, upper_workload, mid_workload, floor_workload, " +
            "risk, upper_cost, mid_cost, floor_cost, upper_adjusted_cost, mid_adjusted_cost, floor_adjusted_cost, direct_nonhuman_cost,big_project_id " +
            "straight_quality_grade, labor_cost_rate) VALUES (#{projectId}, #{projectName}, #{adjustedFP}, #{upperPDR}, #{midPDR}, #{floorPDR}, #{businessDomain}, " +
            "#{applicationType}, #{qualityCharacteristic}, #{developLanguage}, #{teamBackground}, #{upperWorkload}, #{midWorkload}, " +
            "#{floorWorkload}, #{risk}, #{upperCost}, #{midCost}, #{floorCost}, #{upperAdjustedCost}, #{midAdjustedCost}, " +
            "#{floorAdjustedCost}, #{directNonhumanCost}, #{straightQualityGrade}, #{laborCostRate},#{bigProjectId})")
    int insert(CostEvaluation costEvaluation);

    @Update("UPDATE cost_evaluation SET project_name = #{projectName}, adjustedFP = #{adjustedFP}, upper_PDR = #{upperPDR}, " +
            "mid_PDR = #{midPDR}, floor_PDR = #{floorPDR}, business_domain = #{businessDomain}, application_type = #{applicationType}, " +
            "quality_characteristic = #{qualityCharacteristic}, develop_language = #{developLanguage}, team_background = #{teamBackground}, " +
            "upper_workload = #{upperWorkload}, mid_workload = #{midWorkload}, floor_workload = #{floorWorkload}, risk = #{risk}, " +
            "upper_cost = #{upperCost}, mid_cost = #{midCost}, floor_cost = #{floorCost}, upper_adjusted_cost = #{upperAdjustedCost}, " +
            "mid_adjusted_cost = #{midAdjustedCost}, floor_adjusted_cost = #{floorAdjustedCost}, direct_nonhuman_cost = #{directNonhumanCost}, " +
            "straight_quality_grade = #{straightQualityGrade}, labor_cost_rate = #{laborCostRate},big_project_id = #{bigProjectId} WHERE project_id = #{projectId}")
    int update(CostEvaluation costEvaluation);

    @Delete("DELETE FROM cost_evaluation WHERE project_id = #{projectId}")
    int delete(int projectId);
}

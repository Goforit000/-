package com.example.mapper;

import com.example.entity.State;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StateMapper {

    // 1. 获取所有状态的 stateId 和 stateName
    @Select("SELECT state_id, state_name,state_detail,is_official,is_available FROM state")
    List<State> getAllStates();

    // 2. 根据 stateId 查询状态数据
    @Select("SELECT * FROM state WHERE state_id = #{stateId}")
    State getStateById(int stateId);

    // 3. 更新状态数据
    @Update("UPDATE state SET state_name = #{stateName}, application_type_state = #{applicationTypeState}, " +
            "quality_characteristic_state = #{qualityCharacteristicState}, develop_language_state = #{developLanguageState}, " +
            "risk_state = #{riskState}, direct_nonhuman_cost_state = #{directNonhumanCostState}, " +
            "straight_quality_grade_state = #{straightQualityGradeState}, team_background = #{teamBackground}," +
            "state_detail=#{stateDetail},is_official=#{isOfficial},is_available=#{isAvailable} "+
            "WHERE state_id = #{stateId}")
    void updateState(State state);

    // 4. 插入新状态
    @Insert("INSERT INTO state (state_id, state_name, application_type_state, quality_characteristic_state, " +
            "develop_language_state, risk_state, direct_nonhuman_cost_state, straight_quality_grade_state, team_background, state_detail,is_official,is_available) " +
            "VALUES (#{stateId}, #{stateName}, #{applicationTypeState}, #{qualityCharacteristicState}, #{developLanguageState}, " +
            "#{riskState}, #{directNonhumanCostState}, #{straightQualityGradeState}, #{teamBackground}, #{stateDetail},#{isOfficial},#{isAvailable})")
    void insertState(State state);

    // 5. 删除指定状态
    @Delete("DELETE FROM state WHERE state_id = #{stateId}")
    int deleteState(int stateId);
}


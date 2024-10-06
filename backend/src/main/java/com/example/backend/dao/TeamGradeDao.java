package com.example.backend.dao;

import com.example.backend.model.TeamGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamGradeDao extends JpaRepository<TeamGrade, Integer> {
    Optional<TeamGrade> findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(int teamId, int sprintId, int detailId, int evaluatorId);
    Optional<TeamGrade> findByTeamIdAndSprintIdAndDetailId(int teamId, int sprintId, int detailId);

}

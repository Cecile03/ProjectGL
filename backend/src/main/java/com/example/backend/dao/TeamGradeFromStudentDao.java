package com.example.backend.dao;

import com.example.backend.model.TeamGradeFromStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamGradeFromStudentDao extends JpaRepository<TeamGradeFromStudent, Integer> {

    Optional<TeamGradeFromStudent> findByTeamNotingIdAndTeamToNoteIdAndSprintId(int teamNotingId, int teamToNoteId, int sprintId);

    Optional<List<TeamGradeFromStudent>> findByTeamToNoteIdAndSprintId(int teamToNoteId, int sprintId);
}

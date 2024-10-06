package com.example.backend.dao;

import com.example.backend.model.ProjectGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectGradeDao extends JpaRepository<ProjectGrade, Integer> {

    Optional<ProjectGrade> findByUserIdAndSprintId(int userId, int sprintId);
}

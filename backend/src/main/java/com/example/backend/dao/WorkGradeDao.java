package com.example.backend.dao;

import com.example.backend.model.PresentationGrade;
import com.example.backend.model.WorkGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkGradeDao extends JpaRepository<WorkGrade, Integer> {

    Optional<WorkGrade> findByUserIdAndSprintId(int userId, int sprintId);

}

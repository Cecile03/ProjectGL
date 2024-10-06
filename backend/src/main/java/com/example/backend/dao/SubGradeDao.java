package com.example.backend.dao;

import com.example.backend.model.GradeTypes;
import com.example.backend.model.SubGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SubGradeDao extends JpaRepository<SubGrade, Integer> {
    Optional<SubGrade> findByUserIdAndSprintIdAndGradeType(int userId, int sprintId, GradeTypes gradeType);

}

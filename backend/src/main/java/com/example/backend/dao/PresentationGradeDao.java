package com.example.backend.dao;

import com.example.backend.model.PresentationGrade;
import com.example.backend.model.SubGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PresentationGradeDao extends JpaRepository<PresentationGrade, Integer> {

    Optional<PresentationGrade> findByUserIdAndSprintId(int userId, int sprintId);

}

package com.example.backend.dao;

import com.example.backend.model.InitialGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InitialGradeDao extends JpaRepository<InitialGrade, Integer> {

    Optional<InitialGrade> findByUserIdAndSprintId(int userId, int sprintId);

}

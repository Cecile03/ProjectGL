package com.example.backend.dao;

import com.example.backend.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationDao extends JpaRepository<Evaluation, Integer> {
    Optional<Evaluation> findByEvaluatorIdAndSubGradeId(int userId, int subGradeId);
}

package com.example.backend.dao;

import com.example.backend.model.BMValidation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BMValidationDao extends JpaRepository<BMValidation, Integer> {

    BMValidation findByUserIdAndTeamIdAndSprintId(int userId, int teamId, int sprintId);
    List<BMValidation> findAllByTeamIdAndSprintId(int teamId, int sprintId);

}

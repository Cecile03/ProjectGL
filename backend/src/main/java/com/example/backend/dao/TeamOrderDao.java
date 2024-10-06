package com.example.backend.dao;

import com.example.backend.model.TeamOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamOrderDao extends JpaRepository<TeamOrder, Integer> {

    Optional<TeamOrder> findByTeamIdAndSprintId(int teamId, int sprintId);
}

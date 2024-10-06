package com.example.backend.dao;

import com.example.backend.model.Team;
import com.example.backend.model.User;
import com.example.backend.model.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTeamDao extends JpaRepository<UserTeam, Integer> {
    Optional<UserTeam> findByUserAndTeam(User user, Team team);
    Optional<UserTeam> findByUser(User user);
    List<UserTeam> findByTeam(Team team);
}

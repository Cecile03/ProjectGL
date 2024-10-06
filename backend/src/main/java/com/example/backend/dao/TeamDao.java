package com.example.backend.dao;

import com.example.backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TeamDao extends JpaRepository<Team, Integer> {
    Optional<Team> findById(int id);
}

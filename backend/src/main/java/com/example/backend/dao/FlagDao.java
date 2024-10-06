package com.example.backend.dao;

import com.example.backend.model.Flag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlagDao extends JpaRepository<Flag, Integer> {
}
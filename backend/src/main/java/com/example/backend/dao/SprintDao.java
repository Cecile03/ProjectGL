package com.example.backend.dao;

import com.example.backend.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintDao extends JpaRepository<Sprint, Integer> {

}

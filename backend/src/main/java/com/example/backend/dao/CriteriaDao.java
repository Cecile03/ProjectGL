package com.example.backend.dao;

import com.example.backend.model.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CriteriaDao extends JpaRepository<Criteria, Integer> {

}

package com.example.backend.dao;

import com.example.backend.model.GradeScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeScaleDao extends JpaRepository<GradeScale, Integer> {

}

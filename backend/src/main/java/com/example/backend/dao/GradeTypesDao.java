package com.example.backend.dao;

import com.example.backend.model.GradeTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeTypesDao extends JpaRepository<GradeTypes, Integer> {
    Optional<GradeTypes> findById(int id);
}

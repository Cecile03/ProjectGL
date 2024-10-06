package com.example.backend.dao;

import com.example.backend.model.UserFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFlagDao extends JpaRepository<UserFlag, Integer> {
    List<UserFlag> findByFlag_Id(int flagId);
}
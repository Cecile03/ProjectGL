package com.example.backend.dao;

import com.example.backend.model.Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailDao extends JpaRepository<Detail, Integer> {

    List<Detail> findByCategoryId(int categoryId);
}

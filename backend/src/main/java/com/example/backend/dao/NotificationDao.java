package com.example.backend.dao;

import com.example.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationDao extends JpaRepository<Notification, Integer> {
    List<Notification> findByReceiverId(int userId);
    List<Notification> findByEmitterId(int userId);
}

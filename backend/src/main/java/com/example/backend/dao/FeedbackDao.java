package com.example.backend.dao;

import com.example.backend.model.Feedback;
import com.example.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackDao extends JpaRepository<Feedback, Integer> {

    Optional<List<Post>> findByTeamIdAndSprintId(int teamId, int sprintId);

}

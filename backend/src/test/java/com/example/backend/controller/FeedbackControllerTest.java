package com.example.backend.controller;

import com.example.backend.dto.PostDTO;
import com.example.backend.model.Feedback;
import com.example.backend.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FeedbackControllerTest {

    @InjectMocks
    private FeedbackController feedbackController;

    @Mock
    private FeedbackService feedbackService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveFeedback_shouldReturnOkWhenSuccessful() {
        PostDTO postDTO = new PostDTO();
        Feedback feedback = new Feedback();
        when(feedbackService.fromDTO(postDTO)).thenReturn(feedback);

        ResponseEntity<Void> response = feedbackController.saveFeedback(postDTO);

        assertEquals(ResponseEntity.ok().build(), response);
        verify(feedbackService, times(1)).savePost(feedback);
    }

    @Test
    void saveFeedback_shouldReturnBadRequestWhenExceptionThrown() {
        PostDTO postDTO = new PostDTO();
        when(feedbackService.fromDTO(postDTO)).thenThrow(new RuntimeException());

        ResponseEntity<Void> response = feedbackController.saveFeedback(postDTO);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void getFeedback_shouldReturnOkWhenSuccessful() {
        int teamId = 1;
        int sprintId = 1;
        List<PostDTO> expectedPosts = Collections.singletonList(new PostDTO());
        when(feedbackService.getPostByTeamIdAndSprintId(teamId, sprintId)).thenReturn(expectedPosts);

        ResponseEntity<List<PostDTO>> response = feedbackController.getFeedback(teamId, sprintId);

        assertEquals(ResponseEntity.ok(expectedPosts), response);
    }

    @Test
    void getFeedback_shouldReturnBadRequestWhenExceptionThrown() {
        int teamId = 1;
        int sprintId = 1;
        when(feedbackService.getPostByTeamIdAndSprintId(teamId, sprintId)).thenThrow(new RuntimeException());

        ResponseEntity<List<PostDTO>> response = feedbackController.getFeedback(teamId, sprintId);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void deleteFeedback_shouldReturnOkWhenSuccessful() {
        PostDTO postDTO = new PostDTO();
        Feedback feedback = new Feedback();
        when(feedbackService.fromDTO(postDTO)).thenReturn(feedback);

        ResponseEntity<Void> response = feedbackController.deleteFeedback(postDTO);

        assertEquals(ResponseEntity.ok().build(), response);
        verify(feedbackService, times(1)).deletePost(feedback);
    }

    @Test
    void deleteFeedback_shouldReturnBadRequestWhenExceptionThrown() {
        PostDTO postDTO = new PostDTO();
        when(feedbackService.fromDTO(postDTO)).thenThrow(new RuntimeException());

        ResponseEntity<Void> response = feedbackController.deleteFeedback(postDTO);

        assertEquals(ResponseEntity.status(400).build(), response);
    }
}

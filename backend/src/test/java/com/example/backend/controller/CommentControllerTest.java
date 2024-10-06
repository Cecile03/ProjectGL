package com.example.backend.controller;

import com.example.backend.dto.PostDTO;
import com.example.backend.model.Comment;
import com.example.backend.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveComment_shouldReturnOkWhenNoExceptionThrown() {
        // Setup
        PostDTO postDTO = new PostDTO();
        Comment comment = new Comment();
        when(commentService.fromDTO(postDTO)).thenReturn(comment);
        doNothing().when(commentService).updatePost(comment);

        // Execution
        ResponseEntity<Void> response = commentController.saveComment(postDTO);

        // Verification
        assertEquals(200, response.getStatusCodeValue());
        verify(commentService, times(1)).updatePost(comment);
    }

    @Test
    void saveComment_shouldReturnBadRequestWhenExceptionThrown() {
        // Setup
        PostDTO postDTO = new PostDTO();
        when(commentService.fromDTO(postDTO)).thenThrow(new RuntimeException());

        // Execution
        ResponseEntity<Void> response = commentController.saveComment(postDTO);

        // Verification
        assertEquals(400, response.getStatusCodeValue());
        verify(commentService, times(1)).fromDTO(postDTO);
    }

    @Test
    void getCommentsByTeamAndSprint_shouldReturnOkWhenNoExceptionThrown() {
        // Setup
        int teamId = 1;
        int sprintId = 1;
        List<PostDTO> posts = Arrays.asList(new PostDTO(), new PostDTO());
        when(commentService.getPostByTeamIdAndSprintId(teamId, sprintId)).thenReturn(posts);

        // Execution
        ResponseEntity<List<PostDTO>> response = commentController.getCommentsByTeamAndSprint(teamId, sprintId);

        // Verification
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(posts, response.getBody());
        verify(commentService, times(1)).getPostByTeamIdAndSprintId(teamId, sprintId);
    }

    @Test
    void getCommentsByTeamAndSprint_shouldReturnBadRequestWhenExceptionThrown() {
        // Setup
        int teamId = 1;
        int sprintId = 1;
        when(commentService.getPostByTeamIdAndSprintId(teamId, sprintId)).thenThrow(new RuntimeException());

        // Execution
        ResponseEntity<List<PostDTO>> response = commentController.getCommentsByTeamAndSprint(teamId, sprintId);

        // Verification
        assertEquals(400, response.getStatusCodeValue());
        verify(commentService, times(1)).getPostByTeamIdAndSprintId(teamId, sprintId);
    }

    @Test
    void deleteComment_shouldReturnOkWhenNoExceptionThrown() {
        // Setup
        PostDTO postDTO = new PostDTO();
        Comment comment = new Comment();
        when(commentService.fromDTO(postDTO)).thenReturn(comment);
        doNothing().when(commentService).deletePost(comment);

        // Execution
        ResponseEntity<Void> response = commentController.deleteComment(postDTO);

        // Verification
        assertEquals(200, response.getStatusCodeValue());
        verify(commentService, times(1)).deletePost(comment);
    }

    @Test
    void deleteComment_shouldReturnBadRequestWhenExceptionThrown() {
        // Setup
        PostDTO postDTO = new PostDTO();
        when(commentService.fromDTO(postDTO)).thenThrow(new RuntimeException());

        // Execution
        ResponseEntity<Void> response = commentController.deleteComment(postDTO);

        // Verification
        assertEquals(400, response.getStatusCodeValue());
        verify(commentService, times(1)).fromDTO(postDTO);
    }
}

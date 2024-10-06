package com.example.backend.service;

import com.example.backend.dao.FeedbackDao;
import com.example.backend.dto.PostDTO;
import com.example.backend.dto.SprintDTO;
import com.example.backend.dto.TeamSendDTO;
import com.example.backend.dto.UserSendDTO;
import com.example.backend.model.Feedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedbackServiceTest {

    @Mock
    private FeedbackDao feedbackDao;

    @Mock
    private TeamService teamService;

    @Mock
    private SprintService sprintService;

    @Mock
    private UserService userService;


    @InjectMocks
    private FeedbackService feedbackService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldConvertFromDTO() {
        UserSendDTO user = new UserSendDTO();
        TeamSendDTO team = new TeamSendDTO();
        SprintDTO sprint = new SprintDTO();
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Test Title");
        postDTO.setContent("Test Content");
        postDTO.setEmitter(user);
        postDTO.setTeam(team);
        postDTO.setSprint(sprint);

        Feedback feedback = feedbackService.fromDTO(postDTO);

        assertEquals(postDTO.getTitle(), feedback.getTitle());
        assertEquals(postDTO.getContent(), feedback.getContent());
    }

    @Test
    void shouldSaveFeedbackPost() {
        Feedback feedback = new Feedback();
        feedbackService.savePost(feedback);

        verify(feedbackDao, times(1)).save(feedback);
    }

    @Test
    void shouldUpdateExistingFeedbackPost() {
        Feedback feedback = new Feedback();
        feedback.setId(1);
        feedback.setTitle("Updated Title");
        feedback.setContent("Updated Content");

        Feedback existingFeedback = new Feedback();
        existingFeedback.setId(1);
        when(feedbackDao.findById(feedback.getId())).thenReturn(Optional.of(existingFeedback));

        feedbackService.updatePost(feedback);

        verify(feedbackDao, times(1)).save(existingFeedback);
        assertEquals(feedback.getTitle(), existingFeedback.getTitle());
        assertEquals(feedback.getContent(), existingFeedback.getContent());
    }

    @Test
    void shouldSaveNewFeedbackPostWhenUpdatingNonExistingPost() {
        Feedback feedback = new Feedback();
        feedback.setId(1);
        when(feedbackDao.findById(feedback.getId())).thenReturn(Optional.empty());

        feedbackService.updatePost(feedback);

        verify(feedbackDao, times(1)).save(feedback);
    }

    @Test
    void shouldReturnPostByTeamIdAndSprintId() {
        int teamId = 1;
        int sprintId = 1;
        Feedback feedback1 = new Feedback();
        Feedback feedback2 = new Feedback();
        List<Feedback> feedbacks = Arrays.asList(feedback1, feedback2);
        doReturn(Optional.of(feedbacks)).when(feedbackDao).findByTeamIdAndSprintId(teamId, sprintId);

        List<PostDTO> postDTOs = feedbackService.getPostByTeamIdAndSprintId(teamId, sprintId);

        assertEquals(feedbacks.size(), postDTOs.size());
    }

    @Test
    void shouldThrowExceptionWhenNoPostByTeamIdAndSprintId() {
        int teamId = 1;
        int sprintId = 1;
        when(feedbackDao.findByTeamIdAndSprintId(teamId, sprintId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> feedbackService.getPostByTeamIdAndSprintId(teamId, sprintId));

        String expectedMessage = "No feedbacks found for the given teamId and sprintId";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldDeleteFeedbackPost() {
        Feedback feedback = new Feedback();
        feedbackService.deletePost(feedback);

        verify(feedbackDao, times(1)).delete(feedback);
    }
}
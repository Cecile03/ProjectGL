package com.example.backend.service;

import com.example.backend.dao.*;
import com.example.backend.dto.SprintDTO;
import com.example.backend.model.Sprint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SprintServiceTest {

    @InjectMocks
    private SprintService sprintService;

    @Mock
    private TeamGradeDao teamGradeDao;

    @Mock
    private SprintDao sprintDao;

    @Mock
    private TeamGradeFromStudentDao teamGradeFromStudentDao;

    @Mock
    private TeamOrderDao teamOrderDao;

    @Mock
    private FeedbackDao feedbackDao;

    @Mock
    private CommentDao commentDao;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        Sprint sprint = new Sprint();
        when(sprintDao.findAll()).thenReturn(List.of(sprint));
        List<Sprint> result = sprintService.getAll();
        assertEquals(1, result.size());
        verify(sprintDao, times(1)).findAll();
    }

    @Test
    void testGetSprintById() {
        Sprint sprint = new Sprint();
        when(sprintDao.findById(anyInt())).thenReturn(Optional.of(sprint));
        Sprint result = sprintService.getSprintById(1);
        assertEquals(sprint, result);
        verify(sprintDao, times(1)).findById(anyInt());
    }

    @Test
    void testGetSprintByIdNotFound() {
        when(sprintDao.findById(anyInt())).thenThrow(new RuntimeException("Sprint not found"));
        assertThrows(RuntimeException.class, () -> sprintService.getSprintById(1));
    }

    @Test
    void testUpdate() {
        // Create a Sprint object to be updated
        Sprint oldSprint = new Sprint();
        oldSprint.setId(1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.FEBRUARY, 1);
        oldSprint.setStartDate(new Date(calendar.getTimeInMillis()));
        oldSprint.setEndDate(new Date(calendar.getTimeInMillis()));
        oldSprint.setEndType("Old");

        // Create a Sprint object with new data
        Sprint newSprintData = new Sprint();
        newSprintData.setId(1);
        calendar.set(2022, Calendar.FEBRUARY, 1);
        newSprintData.setStartDate(new Date(calendar.getTimeInMillis()));
        newSprintData.setEndDate(new Date(calendar.getTimeInMillis()));
        newSprintData.setEndType("New");

        // Mock the findById method to return the oldSprint
        when(sprintDao.findById(1)).thenReturn(Optional.of(oldSprint));

        // Call the update method
        sprintService.update(newSprintData);

        // Verify that the save method was called with the newSprintData
        verify(sprintDao, times(1)).save(any(Sprint.class));

        // Assert that the oldSprint's fields were updated
        assertEquals(newSprintData.getStartDate(), oldSprint.getStartDate());
        assertEquals(newSprintData.getEndDate(), oldSprint.getEndDate());
        assertEquals(newSprintData.getEndType(), oldSprint.getEndType());
    }

    @Test
    void testUpdateSprintNotFound() {
        // Create a Sprint object with new data
        Sprint newSprintData = new Sprint();
        newSprintData.setId(1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, Calendar.FEBRUARY, 1);
        newSprintData.setStartDate(new Date(calendar.getTimeInMillis()));
        newSprintData.setEndDate(new Date(calendar.getTimeInMillis()));
        newSprintData.setEndType("New");

        // Mock the findById method to return an empty Optional
        when(sprintDao.findById(anyInt())).thenReturn(Optional.empty());

        // Call the update method
        sprintService.update(newSprintData);

        // Verify that the save method was called with the newSprintData
        verify(sprintDao, times(1)).save(newSprintData);
    }

    @Test
    void testFromSprintDTO() {
        // Create a SprintDTO object
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setId(1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, Calendar.FEBRUARY, 1);
        sprintDTO.setStartDate(new Date(calendar.getTimeInMillis()));
        sprintDTO.setEndDate(new Date(calendar.getTimeInMillis()));
        sprintDTO.setEndType("New");

        // Call the fromSprintDTO method
        Sprint sprint = sprintService.fromSprintDTO(sprintDTO);

        // Assert that the Sprint's fields match those of the SprintDTO
        assertEquals(sprintDTO.getId(), sprint.getId());
        assertEquals(sprintDTO.getStartDate(), sprint.getStartDate());
        assertEquals(sprintDTO.getEndDate(), sprint.getEndDate());
        assertEquals(sprintDTO.getEndType(), sprint.getEndType());
    }

    @Test
    void testToDTO() {
        // Create a Sprint object
        Sprint sprint = new Sprint();
        sprint.setId(1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, Calendar.FEBRUARY, 1);
        sprint.setStartDate(new Date(calendar.getTimeInMillis()));
        sprint.setEndDate(new Date(calendar.getTimeInMillis()));
        sprint.setEndType("New");

        // Mock the save method to return the same Sprint object
        when(sprintDao.save(any(Sprint.class))).thenReturn(sprint);

        // Call the toDTO method
        SprintDTO sprintDTO = sprintService.toDTO(sprint);

        // Assert that the SprintDTO's fields match those of the Sprint
        assertEquals(sprint.getStartDate(), sprintDTO.getStartDate());
        assertEquals(sprint.getEndDate(), sprintDTO.getEndDate());
        assertEquals(sprint.getEndType(), sprintDTO.getEndType());
    }

    @Test
    void deleteAllShouldCallAllDeleteMethods() {
        sprintService.deleteAll();

        verify(sprintDao, times(1)).deleteAll();
        verify(teamGradeFromStudentDao, times(1)).deleteAll();
        verify(teamOrderDao, times(1)).deleteAll();
        verify(feedbackDao, times(1)).deleteAll();
        verify(commentDao, times(1)).deleteAll();
        verify(teamGradeDao, times(1)).deleteAll();
    }

    @Test
    void getAllSprints_shouldReturnAllSprints() {
        // Given
        Sprint sprint1 = new Sprint();
        sprint1.setId(1);
        Sprint sprint2 = new Sprint();
        sprint2.setId(2);
        List<Sprint> expectedSprints = Arrays.asList(sprint1, sprint2);

        when(sprintDao.findAll()).thenReturn(expectedSprints);

        // When
        List<Sprint> result = sprintService.getAllSprints();

        // Then
        assertEquals(expectedSprints, result);
        verify(sprintDao, times(1)).findAll();
    }
}
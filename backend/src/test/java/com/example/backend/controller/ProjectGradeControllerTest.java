package com.example.backend.controller;

import com.example.backend.model.ProjectGrade;
import com.example.backend.service.ProjectGradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProjectGradeControllerTest {

    @InjectMocks
    private ProjectGradeController projectGradeController;

    @Mock
    private ProjectGradeService projectGradeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveProjectGrade() {
        int userId = 1;
        int sprintId = 1;

        when(projectGradeService.saveProjectGrade(userId, sprintId)).thenReturn(null);

        projectGradeController.saveProjectGrade(userId, sprintId);

        verify(projectGradeService, times(1)).saveProjectGrade(userId, sprintId);
    }

    @Test
    void testSaveProjectGrade_ExceptionThrown() {
        int userId = 1;
        int sprintId = 1;

        when(projectGradeService.saveProjectGrade(userId, sprintId)).thenThrow(new RuntimeException());

        ResponseEntity<ProjectGrade> response = projectGradeController.saveProjectGrade(userId, sprintId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateProjectGrade() {
        int userId = 1;
        int sprintId = 1;

        when(projectGradeService.updateProjectGrade(userId, sprintId)).thenReturn(null);

        projectGradeController.updateProjectGrade(userId, sprintId);

        verify(projectGradeService, times(1)).updateProjectGrade(userId, sprintId);
    }

    @Test
    void testUpdateProjectGrade_ExceptionThrown() {
        int userId = 1;
        int sprintId = 1;

        when(projectGradeService.updateProjectGrade(userId, sprintId)).thenThrow(new RuntimeException());

        ResponseEntity<ProjectGrade> response = projectGradeController.updateProjectGrade(userId, sprintId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getProjectGradeByUserIdAndSprintId() {
        int userId = 1;
        int sprintId = 1;
        ProjectGrade projectGrade = new ProjectGrade();

        when(projectGradeService.getProjectGradeByUserIdAndSprintId(userId, sprintId)).thenReturn(projectGrade);

        projectGradeController.getProjectGradeByUserIdAndSprintId(userId, sprintId);

        verify(projectGradeService, times(1)).getProjectGradeByUserIdAndSprintId(userId, sprintId);
    }

    @Test
    void testGetProjectGradeByUserIdAndSprintId_ExceptionThrown() {
        int userId = 1;
        int sprintId = 1;

        when(projectGradeService.getProjectGradeByUserIdAndSprintId(userId, sprintId)).thenThrow(new RuntimeException());

        ResponseEntity<ProjectGrade> response = projectGradeController.getProjectGradeByUserIdAndSprintId(userId, sprintId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAll_shouldReturnOkWhenNoExceptionThrown() {
        // Arrange
        doNothing().when(projectGradeService).updateAllProjectGrades();

        // Act
        ResponseEntity<Void> response = projectGradeController.getAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAll_shouldReturnBadRequestWhenExceptionThrown() {
        // Arrange
        doThrow(new RuntimeException()).when(projectGradeService).updateAllProjectGrades();

        // Act
        ResponseEntity<Void> response = projectGradeController.getAll();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
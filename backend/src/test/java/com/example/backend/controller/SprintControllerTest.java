package com.example.backend.controller;

import com.example.backend.dto.SprintDTO;
import com.example.backend.model.Sprint;
import com.example.backend.service.SprintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SprintControllerTest {

    @InjectMocks
    private SprintController sprintController;

    @Mock
    private SprintService sprintService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSprint_shouldReturnOkWhenSuccessful() {
        SprintDTO sprintDTO = new SprintDTO();
        Sprint sprint = new Sprint();
        when(sprintService.fromSprintDTO(sprintDTO)).thenReturn(sprint);
        doNothing().when(sprintService).saveSprint(sprint);

        ResponseEntity<Void> response = sprintController.createSprint(sprintDTO);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void createSprint_shouldReturnInternalServerErrorWhenExceptionThrown() {
        SprintDTO sprintDTO = new SprintDTO();
        when(sprintService.fromSprintDTO(sprintDTO)).thenThrow(new RuntimeException());

        ResponseEntity<Void> response = sprintController.createSprint(sprintDTO);

        assertEquals(ResponseEntity.status(500).build(), response);
    }

    @Test
    void update_shouldReturnOkWhenSuccessful() {
        SprintDTO sprintDTO = new SprintDTO();
        Sprint sprint = new Sprint();
        when(sprintService.fromSprintDTO(sprintDTO)).thenReturn(sprint);
        doNothing().when(sprintService).update(sprint);

        ResponseEntity<Void> response = sprintController.update(sprintDTO);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void update_shouldReturnInternalServerErrorWhenExceptionThrown() {
        SprintDTO sprintDTO = new SprintDTO();
        when(sprintService.fromSprintDTO(sprintDTO)).thenThrow(new RuntimeException());

        ResponseEntity<Void> response = sprintController.update(sprintDTO);

        assertEquals(ResponseEntity.status(500).build(), response);
    }

    @Test
    void getSprint_shouldReturnSprintsWhenSuccessful() {
        List<Sprint> expectedSprints = Collections.singletonList(new Sprint());
        when(sprintService.getAll()).thenReturn(expectedSprints);

        ResponseEntity<List<Sprint>> response = sprintController.getSprint();

        assertEquals(ResponseEntity.ok(expectedSprints), response);
    }

    @Test
    void getSprint_shouldReturnInternalServerErrorWhenExceptionThrown() {
        when(sprintService.getAll()).thenThrow(new RuntimeException());

        ResponseEntity<List<Sprint>> response = sprintController.getSprint();

        assertEquals(ResponseEntity.status(500).build(), response);
    }

    @Test
    void deleteAllSprints_shouldReturnOkWhenSuccessful() {
        doNothing().when(sprintService).deleteAll();

        ResponseEntity<Void> response = sprintController.deleteAllSprints();

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void deleteAllSprints_shouldReturnInternalServerErrorWhenExceptionThrown() {
        doThrow(new RuntimeException()).when(sprintService).deleteAll();

        ResponseEntity<Void> response = sprintController.deleteAllSprints();

        assertEquals(ResponseEntity.status(500).build(), response);
    }

    @Test
    void getSprintById_shouldReturnSprint_whenNoExceptionThrown() {
        // Arrange
        int sprintId = 1;
        Sprint expectedSprint = new Sprint();
        when(sprintService.getSprintById(sprintId)).thenReturn(expectedSprint);

        // Act
        ResponseEntity<Sprint> response = sprintController.getSprintById(sprintId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSprint, response.getBody());
    }

    @Test
    void getSprintById_shouldReturnInternalServerError_whenExceptionThrown() {
        // Arrange
        int sprintId = 1;
        when(sprintService.getSprintById(sprintId)).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<Sprint> response = sprintController.getSprintById(sprintId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
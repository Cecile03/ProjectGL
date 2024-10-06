package com.example.backend.controller;

import com.example.backend.model.TeamGradeFromStudent;
import com.example.backend.service.TeamGradeFromStudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TeamGradeFromStudentControllerTest {

    @InjectMocks
    private TeamGradeFromStudentController teamGradeFromStudentController;

    @Mock
    private TeamGradeFromStudentService teamGradeFromStudentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveTeamGradeFromStudent_shouldReturnOkWhenSuccessful() {
        doNothing().when(teamGradeFromStudentService).updateTeamGradeFromStudent(anyInt(), anyInt(), anyInt(), anyInt());

        ResponseEntity<Void> response = teamGradeFromStudentController.saveTeamGradeFromStudent(1, 1, 1, 1);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void saveTeamGradeFromStudent_shouldReturnBadRequestWhenExceptionThrown() {
        doThrow(new RuntimeException()).when(teamGradeFromStudentService).updateTeamGradeFromStudent(anyInt(), anyInt(), anyInt(), anyInt());

        ResponseEntity<Void> response = teamGradeFromStudentController.saveTeamGradeFromStudent(1, 1, 1, 1);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void getTeamGradeFromStudent_shouldReturnOkWhenSuccessful() {
        TeamGradeFromStudent expectedTeamGradeFromStudent = new TeamGradeFromStudent();
        when(teamGradeFromStudentService.getTeamGradeFromStudent(anyInt(), anyInt(), anyInt())).thenReturn(expectedTeamGradeFromStudent);

        ResponseEntity<TeamGradeFromStudent> response = teamGradeFromStudentController.getTeamGradeFromStudent(1, 1, 1);

        assertEquals(ResponseEntity.ok(expectedTeamGradeFromStudent), response);
    }

    @Test
    void getTeamGradeFromStudent_shouldReturnBadRequestWhenExceptionThrown() {
        when(teamGradeFromStudentService.getTeamGradeFromStudent(anyInt(), anyInt(), anyInt())).thenThrow(new RuntimeException());

        ResponseEntity<TeamGradeFromStudent> response = teamGradeFromStudentController.getTeamGradeFromStudent(1, 1, 1);

        assertEquals(ResponseEntity.status(400).build(), response);
    }
}

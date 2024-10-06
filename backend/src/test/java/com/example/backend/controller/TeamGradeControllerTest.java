package com.example.backend.controller;

import com.example.backend.model.TeamGrade;
import com.example.backend.service.TeamGradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TeamGradeControllerTest {

    @InjectMocks
    private TeamGradeController teamGradeController;

    @Mock
    private TeamGradeService teamGradeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveTeamGrade_shouldReturnOkWhenSuccessful() {
        doNothing().when(teamGradeService).updateTeamGrade(anyInt(), anyInt(), anyInt(), anyInt(), anyDouble());

        ResponseEntity<Void> response = teamGradeController.saveTeamGrade(1, 1, 1, 1,1.0);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void saveTeamGrade_shouldReturnBadRequestWhenExceptionThrown() {
        doThrow(new RuntimeException()).when(teamGradeService).updateTeamGrade(anyInt(), anyInt(), anyInt(), anyInt(), anyDouble());

        ResponseEntity<Void> response = teamGradeController.saveTeamGrade(1, 1, 1, 1, 1.0);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void getTeamGrade_shouldReturnOkWhenSuccessful() {
        TeamGrade expectedTeamGrade = new TeamGrade();
        when(teamGradeService.getTeamGrade(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(expectedTeamGrade);

        ResponseEntity<TeamGrade> response = teamGradeController.getTeamGrade(1, 1, 1, 1);

        assertEquals(ResponseEntity.ok(expectedTeamGrade), response);
    }

    @Test
    void getTeamGrade_shouldReturnBadRequestWhenExceptionThrown() {
        when(teamGradeService.getTeamGrade(anyInt(), anyInt(), anyInt(), anyInt())).thenThrow(new RuntimeException());

        ResponseEntity<TeamGrade> response = teamGradeController.getTeamGrade(1, 1, 1,1);

        assertEquals(ResponseEntity.status(400).build(), response);
    }
}

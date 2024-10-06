package com.example.backend.controller;

import com.example.backend.dto.BonusMalusDTO;
import com.example.backend.model.BonusMalus;
import com.example.backend.service.BonusMalusService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BonusMalusControllerTest {

    @Mock
    private BonusMalusService bonusMalusService;

    private BonusMalusController bonusMalusController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        bonusMalusController = new BonusMalusController(bonusMalusService);
    }

    @Test
    void getAllBonusMalus_shouldReturnOkWhenNoExceptionThrown() {
        int sprintId = 1;
        when(bonusMalusService.getAllBonusMalus(sprintId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<List<BonusMalusDTO>>> response = bonusMalusController.getAllBonusMalus(sprintId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllBonusMalus_shouldReturnBadRequestWhenExceptionThrown() {
        int sprintId = 1;
        when(bonusMalusService.getAllBonusMalus(sprintId)).thenThrow(new RuntimeException());

        ResponseEntity<List<List<BonusMalusDTO>>> response = bonusMalusController.getAllBonusMalus(sprintId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getTeamBM_shouldReturnOkWhenNoExceptionThrown() {
        int teamId = 1;
        int sprintId = 1;
        when(bonusMalusService.getLBmByTeamAndSprint(teamId, sprintId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<BonusMalusDTO>> response = bonusMalusController.getTeamLBM(teamId, sprintId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTeamBM_shouldReturnBadRequestWhenExceptionThrown() {
        int teamId = 1;
        int sprintId = 1;
        when(bonusMalusService.getLBmByTeamAndSprint(teamId, sprintId)).thenThrow(new RuntimeException());

        ResponseEntity<List<BonusMalusDTO>> response = bonusMalusController.getTeamLBM(teamId, sprintId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addBonusMalus_shouldReturnOkWhenNoExceptionThrown() {
        int teamId = 1;
        int sprintId = 1;
        List<BonusMalusDTO> bonusMalusList = Collections.emptyList();
        when(bonusMalusService.addBonusMalusByTeam(teamId, sprintId, bonusMalusList)).thenReturn(bonusMalusList);

        ResponseEntity<List<BonusMalusDTO>> response = bonusMalusController.addBonusMalus(teamId, sprintId, bonusMalusList);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addBonusMalus_shouldReturnNotFoundWhenEntityNotFoundExceptionThrown() {
        int teamId = 1;
        int sprintId = 1;
        List<BonusMalusDTO> bonusMalusList = Collections.emptyList();
        when(bonusMalusService.addBonusMalusByTeam(teamId, sprintId, bonusMalusList)).thenThrow(new EntityNotFoundException());

        ResponseEntity<List<BonusMalusDTO>> response = bonusMalusController.addBonusMalus(teamId, sprintId, bonusMalusList);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void addBonusMalus_shouldReturnInternalServerErrorWhenExceptionThrown() {
        int teamId = 1;
        int sprintId = 1;
        List<BonusMalusDTO> bonusMalusList = Collections.emptyList();
        when(bonusMalusService.addBonusMalusByTeam(teamId, sprintId, bonusMalusList)).thenThrow(new RuntimeException());

        ResponseEntity<List<BonusMalusDTO>> response = bonusMalusController.addBonusMalus(teamId, sprintId, bonusMalusList);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void validateTeamBM_shouldReturnOkWhenNoExceptionThrown() {
        int sprintId = 1;
        int teamId = 1;
        doNothing().when(bonusMalusService).validateTeamBM(teamId, sprintId);

        ResponseEntity response = bonusMalusController.validateTeamBM(teamId, sprintId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void validateTeamBM_shouldReturnBadRequestWhenExceptionThrown() {
        int sprintId = 1;
        int teamId = 1;
        doThrow(new RuntimeException()).when(bonusMalusService).validateTeamBM(teamId, sprintId);

        ResponseEntity response = bonusMalusController.validateTeamBM(teamId, sprintId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getMembersWhoValidateBM_shouldReturnOkWhenNoExceptionThrown() {
        int teamId = 1;
        int sprintId = 1;
        when(bonusMalusService.getMembersWhoValidateBM(teamId, sprintId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Integer>> response = bonusMalusController.getMembersWhoValidateBM(teamId, sprintId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getMembersWhoValidateBM_shouldReturnBadRequestWhenExceptionThrown() {
        int teamId = 1;
        int sprintId = 1;
        when(bonusMalusService.getMembersWhoValidateBM(teamId, sprintId)).thenThrow(new RuntimeException());

        ResponseEntity<List<Integer>> response = bonusMalusController.getMembersWhoValidateBM(teamId, sprintId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUserBMForSprintByStudent_shouldReturnOkWhenNoExceptionThrown() {
        // Arrange
        int userId = 1;
        int sprintId = 1;
        BonusMalus bonusMalus = new BonusMalus();
        when(bonusMalusService.getBonusMalusStudentByTeamIdAndSprintIdValidated(userId, sprintId)).thenReturn(bonusMalus);

        // Act
        ResponseEntity<BonusMalus> response = bonusMalusController.getUserBMForSprintByStudent(userId, sprintId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bonusMalus, response.getBody());
    }

    @Test
    void getUserBMForSprintByStudent_shouldReturnBadRequestWhenExceptionThrown() {
        // Arrange
        int userId = 1;
        int sprintId = 1;
        when(bonusMalusService.getBonusMalusStudentByTeamIdAndSprintIdValidated(userId, sprintId)).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<BonusMalus> response = bonusMalusController.getUserBMForSprintByStudent(userId, sprintId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUserBMForSprintBySS_shouldReturnOkWhenNoExceptionThrown() {
        // Arrange
        int userId = 1;
        int sprintId = 1;
        BonusMalus bonusMalus = new BonusMalus();
        when(bonusMalusService.getBonusMalusSSByTeamIdAndSprintIdValidated(userId, sprintId)).thenReturn(bonusMalus);

        // Act
        ResponseEntity<BonusMalus> response = bonusMalusController.getUserBMForSprintBySS(userId, sprintId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bonusMalus, response.getBody());
    }

    @Test
    void getUserBMForSprintBySS_shouldReturnBadRequestWhenExceptionThrown() {
        // Arrange
        int userId = 1;
        int sprintId = 1;
        when(bonusMalusService.getBonusMalusSSByTeamIdAndSprintIdValidated(userId, sprintId)).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<BonusMalus> response = bonusMalusController.getUserBMForSprintBySS(userId, sprintId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getTeamUBM_shouldReturnOkWhenNoExceptionThrown() {
        // Arrange
        int teamId = 1;
        int sprintId = 1;
        List<BonusMalusDTO> bonusMalusList = Collections.emptyList();
        when(bonusMalusService.getUBmByTeamAndSprint(teamId, sprintId)).thenReturn(bonusMalusList);

        // Act
        ResponseEntity<List<BonusMalusDTO>> response = bonusMalusController.getTeamUBM(teamId, sprintId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bonusMalusList, response.getBody());
    }

    @Test
    void getTeamUBM_shouldReturnBadRequestWhenExceptionThrown() {
        // Arrange
        int teamId = 1;
        int sprintId = 1;
        when(bonusMalusService.getUBmByTeamAndSprint(teamId, sprintId)).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<List<BonusMalusDTO>> response = bonusMalusController.getTeamUBM(teamId, sprintId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

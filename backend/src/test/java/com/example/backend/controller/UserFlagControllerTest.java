package com.example.backend.controller;

import com.example.backend.dto.FlagDto;
import com.example.backend.dto.UserFlagDto;
import com.example.backend.dto.UserInteract;
import com.example.backend.model.Team;
import com.example.backend.model.User;
import com.example.backend.model.UserFlag;
import com.example.backend.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFlagControllerTest {

    @Mock
    private UserFlagService userFlagService;

    @InjectMocks
    private UserFlagController userFlagController;

    @Mock
    private UserService userService;

    @Mock
    private TeamService teamService;

    @Test
    void getAllUserFlags_shouldReturnList() {
        // Setup
        when(userFlagService.getAllUserFlags()).thenReturn(Collections.emptyList());

        // Execution
        userFlagController.getAllUserFlags();

        // Verification
        verify(userFlagService, times(1)).getAllUserFlags();
    }



    @Test
    void getAllUserFlagByFlagId_shouldReturnList() {
        // Setup
        int flagId = 1;
        when(userFlagService.getAllUserFlagByFlagId(flagId)).thenReturn(Collections.emptyList());

        // Execution
        userFlagController.getAllUserFlagByFlagId(flagId);

        // Verification
        verify(userFlagService, times(1)).getAllUserFlagByFlagId(flagId);
    }

    @Test
    void getAllUserFlagByFlagId_shouldReturnInternalServerErrorWhenExceptionThrown() {
        int flagId = 1;
        when(userFlagService.getAllUserFlagByFlagId(flagId)).thenThrow(new RuntimeException());

        ResponseEntity<List<UserFlag>> response = userFlagController.getAllUserFlagByFlagId(flagId);

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void setValidated_shouldReturnUserFlag() {
        // Setup
        int id = 1;
        boolean validated = true;
        when(userFlagService.setValidated(id, validated)).thenReturn(new UserFlag());

        // Execution
        userFlagController.setValidated(id, validated);

        // Verification
        verify(userFlagService, times(1)).setValidated(id, validated);
    }

    @Test
    void setValidated_shouldReturnInternalServerErrorWhenExceptionThrown() {
        int id = 1;
        boolean validated = true;
        when(userFlagService.setValidated(id, validated)).thenThrow(new RuntimeException());

        ResponseEntity<UserFlag> response = userFlagController.setValidated(id, validated);

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void deleteUserFlagsByFlagId_shouldReturnNoContentWhenNoExceptionThrown() {
        // Arrange
        int flagId = 1;
        doNothing().when(userFlagService).deleteUserFlagsByFlagId(flagId);

        // Act
        ResponseEntity<Void> response = userFlagController.deleteUserFlagsByFlagId(flagId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteUserFlagsByFlagId_shouldReturnInternalServerErrorWhenExceptionThrown() {
        // Arrange
        int flagId = 1;
        doThrow(new RuntimeException()).when(userFlagService).deleteUserFlagsByFlagId(flagId);

        // Act
        ResponseEntity<Void> response = userFlagController.deleteUserFlagsByFlagId(flagId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void areAllUserFlagsValidatedByFlagId_shouldReturnTrueWhenAllUserFlagsAreValidated() {
        // Arrange
        int flagId = 1;
        when(userFlagService.areAllUserFlagsValidatedByFlagId(flagId)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = userFlagController.areAllUserFlagsValidatedByFlagId(flagId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void areAllUserFlagsValidatedByFlagId_shouldReturnFalseWhenNotAllUserFlagsAreValidated() {
        // Arrange
        int flagId = 1;
        when(userFlagService.areAllUserFlagsValidatedByFlagId(flagId)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = userFlagController.areAllUserFlagsValidatedByFlagId(flagId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    void createUserFlags_shouldReturnUserFlags() {
        // Arrange
        List<UserFlagDto> userFlagDtos = new ArrayList<>();
        UserFlagDto userFlagDto = new UserFlagDto();
        UserInteract userInteract = new UserInteract();
        userInteract.setId(1);
        FlagDto flagDto = new FlagDto();
        flagDto.setId(1);
        flagDto.setComment("comment");
        userFlagDto.setFlagDto(flagDto);
        userFlagDto.setId(1);
        userFlagDto.setUserId(userInteract);
        userFlagDtos.add(userFlagDto);

        UserFlag expectedUserFlag = new UserFlag();
        expectedUserFlag.setId(1);

        when(userFlagService.saveUserFlag(any(UserFlag.class))).thenReturn(expectedUserFlag);
        when(userService.getUserByIdNotOptional(anyInt())).thenReturn(new User());
        when(teamService.getTeamById(anyInt())).thenReturn(new Team());

        // Act
        List<UserFlag> actualUserFlags = userFlagController.createUserFlags(userFlagDtos);

        // Assert
        assertEquals(1, actualUserFlags.size());
        assertEquals(expectedUserFlag, actualUserFlags.get(0));
        verify(userFlagService, times(1)).saveUserFlag(any(UserFlag.class));
    }
}
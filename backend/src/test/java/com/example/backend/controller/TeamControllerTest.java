package com.example.backend.controller;

import com.example.backend.dto.TeamDTO;
import com.example.backend.dto.UserInteract;
import com.example.backend.model.Team;
import com.example.backend.model.User;
import com.example.backend.service.TeamService;
import com.example.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TeamControllerTest {

    @InjectMocks
    private TeamController teamController;

    @Mock
    private TeamService teamService;

    @Mock
    private UserService userService;

    @BeforeEach
     void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTeams() {
        Team team = new Team();
        when(teamService.getAllTeams()).thenReturn(List.of(team));
        List<Team> result = teamController.getAllTeams();
        assertEquals(1, result.size());
        verify(teamService, times(1)).getAllTeams();
    }

    @Test
    void testGetTeamById() {
        Team team = new Team();
        when(teamService.getTeamById(anyInt())).thenReturn(team);
        Team result = teamController.getTeamById(1);
        assertEquals(team, result);
        verify(teamService, times(1)).getTeamById(anyInt());
    }

    @Test
    void testGetTeam() {
        User user = new User();
        when(teamService.addTeamMember(anyInt(), anyInt())).thenReturn(user);
        ResponseEntity<User> response = teamController.getTeam(1, 1);
        assertEquals(200, response.getStatusCode().value() );
        assertEquals(user, response.getBody());
    }

    @Test
    void getTeams_shouldReturnBadRequestWhenExceptionThrown() {
        when(teamService.addTeamMember(anyInt(), anyInt())).thenThrow(new RuntimeException());

        ResponseEntity<User> response = teamController.getTeam(1, 1);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void testUpdateTeam() {
        TeamDTO teamDTO = new TeamDTO();
        doNothing().when(teamService).updateTeam(any(TeamDTO.class));
        ResponseEntity<Void> response = teamController.updateTeam(teamDTO);
        assertEquals(200, response.getStatusCode().value() );
    }

    @Test
    void updateTeam_shouldReturnBadRequestWhenExceptionThrown() {
        TeamDTO teamDTO = new TeamDTO();
        doThrow(new RuntimeException()).when(teamService).updateTeam(any(TeamDTO.class));

        ResponseEntity<Void> response = teamController.updateTeam(teamDTO);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void testDeleteTeam() {
        doNothing().when(teamService).deleteTeam(anyInt());
        ResponseEntity<Void> response = teamController.deleteTeam(1);
        assertEquals(200, response.getStatusCode().value() );
    }

    @Test
    void deleteTeam_shouldReturnBadRequestWhenExceptionThrown() {
        // Arrange
        doThrow(new RuntimeException()).when(teamService).deleteTeam(anyInt());

        // Act
        ResponseEntity<Void> response = teamController.deleteTeam(1);

        // Assert
        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void testDeleteAllTeam() {
        doNothing().when(teamService).deleteAllTeam();
        ResponseEntity<Void> response = teamController.deleteAllTeam();
        assertEquals(200, response.getStatusCode().value() );
    }

    @Test
    void deleteAllTeam_shouldReturnBadRequestWhenExceptionThrown() {
        // Arrange
        doThrow(new RuntimeException()).when(teamService).deleteAllTeam();

        // Act
        ResponseEntity<Void> response = teamController.deleteAllTeam();

        // Assert
        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void createTeams_shouldConvertUsersCorrectly() {
        // Arrange
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setStudents(Arrays.asList(new UserInteract(), new UserInteract()));
        teamDTO.setTeachers(Arrays.asList(new UserInteract(), new UserInteract(), new UserInteract()));
        teamDTO.setNames(Arrays.asList("Team1", "Team2").toArray(new String[0]));
        teamDTO.setNumberOfTeams(2);
        teamDTO.setNumberOfGirlsPerTeam(1);

        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        User user4 = new User();
        User user5 = new User();

        when(userService.userInteractToUser(teamDTO.getStudents().get(0))).thenReturn(Optional.of(user1));
        when(userService.userInteractToUser(teamDTO.getStudents().get(1))).thenReturn(Optional.of(user2));
        when(userService.userInteractToUser(teamDTO.getTeachers().get(0))).thenReturn(Optional.of(user3));
        when(userService.userInteractToUser(teamDTO.getTeachers().get(1))).thenReturn(Optional.of(user4));
        when(userService.userInteractToUser(teamDTO.getTeachers().get(2))).thenReturn(Optional.of(user5));

        when(teamService.createTeams(anyList(), anyList(), any(String[].class), anyInt(), anyInt())).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<Team>> response = teamController.createTeams(teamDTO);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void createTeams_shouldReturnBadRequestWhenExceptionThrown() {
        TeamDTO teamDTO = new TeamDTO();
        when(teamService.createTeams(anyList(), anyList(), any(String[].class), anyInt(), anyInt())).thenThrow(new RuntimeException());

        ResponseEntity<List<Team>> response = teamController.createTeams(teamDTO);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void getTeam_shouldReturnBadRequestWhenExceptionThrown() {
        // Arrange
        when(teamService.getTeamById(anyInt())).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<Set<User>> response = teamController.getTeam(1);

        // Assert
        assertEquals(ResponseEntity.status(400).build(), response);
    }

}
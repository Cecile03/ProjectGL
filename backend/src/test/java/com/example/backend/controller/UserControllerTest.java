package com.example.backend.controller;

import com.example.backend.dto.UserInteract;
import com.example.backend.model.Team;
import com.example.backend.model.User;
import com.example.backend.service.AuthService;
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

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Mock
    private TeamService teamService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateStudents() {
        UserInteract userInteract = new UserInteract();
        User user = new User();
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.userInteractToUser(any(UserInteract.class))).thenReturn(Optional.of(user));
        doNothing().when(userService).setDefaultRoles(any(User.class));
        doNothing().when(authService).setDefaultPassword(any(User.class));
        when(userService.saveUser(any(User.class))).thenReturn(user);
        ResponseEntity<Void> response = userController.createStudents(List.of(userInteract));
        assertEquals(200, response.getStatusCode().value() );
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void createStudents_shouldDeleteExistingUser() {
        UserInteract userInteract = new UserInteract();
        userInteract.setEmail("existing@example.com");
        User existingUser = new User();
        existingUser.setId(1);
        when(userService.existsByEmail(userInteract.getEmail())).thenReturn(true);
        when(userService.loadUserByEmail(userInteract.getEmail())).thenReturn(existingUser);
        doNothing().when(userService).deleteUser(existingUser.getId());

        userController.createStudents(List.of(userInteract));

        verify(userService, times(1)).deleteUser(existingUser.getId());
    }

    @Test
    void createStudents_shouldReturnBadRequestWhenExceptionThrown() {
        UserInteract userInteract = new UserInteract();
        userInteract.setEmail("test@example.com");
        when(userService.existsByEmail(userInteract.getEmail())).thenThrow(new RuntimeException());

        ResponseEntity<Void> response = userController.createStudents(List.of(userInteract));

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetTeachers() {
        User user = new User();
        when(userService.getTeachers()).thenReturn(List.of(user));
        ResponseEntity<List<User>> response = userController.getTeachers();
        assertEquals(200, response.getStatusCode().value() );
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getTeachers_shouldReturnBadRequestWhenExceptionThrown() {
        when(userService.getTeachers()).thenThrow(new RuntimeException());

        ResponseEntity<List<User>> response = userController.getTeachers();

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetStudents() {
        User user = new User();
        when(userService.getStudents()).thenReturn(List.of(user));
        ResponseEntity<List<User>> response = userController.getStudents();
        assertEquals(200, response.getStatusCode().value() );
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getStudents_shouldReturnBadRequestWhenExceptionThrown() {
        when(userService.getStudents()).thenThrow(new RuntimeException());

        ResponseEntity<List<User>> response = userController.getStudents();

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetAllUsers() {
        User user = new User();
        when(userService.getAllUsers()).thenReturn(List.of(user));
        ResponseEntity<List<User>> response = userController.getAllUsers();
        assertEquals(200, response.getStatusCode().value() );
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getAllUsers_shouldReturnBadRequestWhenExceptionThrown() {
        when(userService.getAllUsers()).thenThrow(new RuntimeException());

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetUserById() {
        User user = new User();
        when(userService.getUserById(anyInt())).thenReturn(user);
        ResponseEntity<User> response = userController.getUserById(1);
        assertEquals(200, response.getStatusCode().value() );
        assertEquals(user, response.getBody());
    }

    @Test
    void getUserById_shouldReturnBadRequestWhenExceptionThrown() {
        when(userService.getUserById(anyInt())).thenThrow(new RuntimeException());

        ResponseEntity<User> response = userController.getUserById(1);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(anyInt());
        ResponseEntity<Void> response = userController.deleteUser(1);
        assertEquals(200, response.getStatusCode().value() );
    }

    @Test
    void deleteUser_shouldReturnBadRequestWhenExceptionThrown() {
        doThrow(new RuntimeException()).when(userService).deleteUser(anyInt());

        ResponseEntity<Void> response = userController.deleteUser(1);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testDeleteStudents() {
        doNothing().when(userService).deleteStudents();
        doNothing().when(teamService).deleteAllTeam();
        ResponseEntity<Void> response = userController.deleteStudents();
        assertEquals(200, response.getStatusCode().value() );
    }

    @Test
    void deleteStudents_shouldReturnBadRequestWhenExceptionThrown() {
        doThrow(new RuntimeException()).when(userService).deleteStudents();
        doNothing().when(teamService).deleteAllTeam();

        ResponseEntity<Void> response = userController.deleteStudents();

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testUpdateUser(){
        UserInteract userInteract = new UserInteract();
        User user = new User();
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.userInteractToUser(any(UserInteract.class))).thenReturn(Optional.of(user));
        when(userService.saveUser(any(User.class))).thenReturn(user);
        ResponseEntity<Void> response = userController.updateUser(List.of(userInteract));
        assertEquals(200, response.getStatusCode().value() );
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateExistingUserWhenEmailExists() {
        UserInteract userInteract = new UserInteract();
        userInteract.setEmail("existinguser@example.com");
        userInteract.setGradePast(90.0);
        User existingUser = new User();
        existingUser.setEmail("existinguser@example.com");

        when(userService.existsByEmail(userInteract.getEmail())).thenReturn(true);
        when(userService.loadUserByEmail(userInteract.getEmail())).thenReturn(existingUser);
        when(userService.updateUser(any(User.class))).thenReturn(existingUser);

        ResponseEntity<Void> response = userController.updateUser(List.of(userInteract));

        assertEquals(200, response.getStatusCode().value());
        verify(userService, times(1)).updateUser(existingUser);
        assertEquals(90.0, existingUser.getGradePast());
    }

    @Test
    void updateUser_shouldReturnBadRequestWhenExceptionThrown() {
        UserInteract userInteract = new UserInteract();
        userInteract.setEmail("existinguser@example.com");
        User existingUser = new User();
        existingUser.setEmail("existinguser@example.com");

        when(userService.existsByEmail(userInteract.getEmail())).thenReturn(true);
        when(userService.loadUserByEmail(userInteract.getEmail())).thenReturn(existingUser);
        when(userService.updateUser(any(User.class))).thenThrow(new RuntimeException());

        ResponseEntity<Void> response = userController.updateUser(List.of(userInteract));

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetTeams() {
        Team team = new Team();
        team.setId(1);
        User user = new User();
        user.setTeams(new HashSet<>(List.of(team)));
        when(userService.getUserById(anyInt())).thenReturn(user);
        ResponseEntity<List<Integer>> response = userController.getTeams(1);
        assertEquals(200, response.getStatusCode().value() );
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getTeams_shouldReturnBadRequestWhenExceptionThrown() {
        when(userService.getUserById(anyInt())).thenThrow(new RuntimeException());

        ResponseEntity<List<Integer>> response = userController.getTeams(1);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testSaveUser() {
        User user = new User();
        when(userService.saveUser(any(User.class))).thenReturn(user);
        ResponseEntity<User> response = userController.saveUser(user);
        assertEquals(200, response.getStatusCode().value() );
        assertEquals(user, response.getBody());
    }

    @Test
    void saveUser_shouldReturnBadRequestWhenExceptionThrown() {
        User user = new User();
        when(userService.saveUser(any(User.class))).thenThrow(new RuntimeException());

        ResponseEntity<User> response = userController.saveUser(user);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testUpdateUser2() {
        User user = new User();
        when(userService.updateUser(any(User.class))).thenReturn(user);
        ResponseEntity<User> response = userController.updateUser(user);
        assertEquals(200, response.getStatusCode().value() );
        assertEquals(user, response.getBody());
    }

    @Test
    void updateUser2_shouldReturnBadRequestWhenExceptionThrown() {
        User user = new User();
        when(userService.updateUser(any(User.class))).thenThrow(new RuntimeException());

        ResponseEntity<User> response = userController.updateUser(user);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void getAllStaff_shouldReturnOkWhenNoExceptionThrown() {
        // Arrange
        List<User> expectedUsers = new ArrayList<>(); // replace with actual list of Users
        when(userService.getAllStaff()).thenReturn(expectedUsers);

        // Act
        ResponseEntity<List<User>> response = userController.getAllStaff();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
    }

    @Test
    void getAllStaff_shouldReturnBadRequestWhenExceptionThrown() {
        // Arrange
        when(userService.getAllStaff()).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<List<User>> response = userController.getAllStaff();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
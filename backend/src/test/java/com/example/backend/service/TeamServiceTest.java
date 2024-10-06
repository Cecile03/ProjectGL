package com.example.backend.service;

import com.example.backend.dao.*;
import com.example.backend.dto.TeamDTO;
import com.example.backend.dto.TeamSendDTO;
import com.example.backend.dto.UserInteract;
import com.example.backend.model.Criteria;
import com.example.backend.model.Team;
import com.example.backend.model.User;
import com.example.backend.model.UserTeam;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamServiceTest {

    @InjectMocks
    private TeamService teamService;

    @Mock
    private UserTeamDao userTeamDao;

    @Mock
    private TeamDao teamDao;


    @Mock
    private UserService userService;

    @Mock
    private CriteriaService criteriaService;

    @Mock
    private CleanupService cleanupService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void testGetAllTeams() {
        Team team1 = new Team();
        Team team2 = new Team();
        when(teamDao.findAll()).thenReturn(Arrays.asList(team1, team2));

        assertEquals(2, teamService.getAllTeams().size());
        verify(teamDao, times(1)).findAll();
    }

    @Test
    void testGetTeamById() {
        int id = 1;
        Team team = new Team();
        when(teamDao.findById(id)).thenReturn(Optional.of(team));

        assertEquals(team, teamService.getTeamById(id));
        verify(teamDao, times(1)).findById(id);
    }

    @Test
    void testCreateTeams() {
        // Prepare test data
        User user1 = new User();
        user1.setGender("male");
        user1.setBachelor(true);
        user1.setGradePast(95.0);

        User user2 = new User();
        user2.setGender("female");
        user2.setBachelor(false);
        user2.setGradePast(90.0);

        User user3 = new User();
        user3.setGender("male");
        user3.setBachelor(false);
        user3.setGradePast(88.0);

        User user4 = new User();
        user4.setGender("female");
        user4.setBachelor(false);
        user4.setGradePast(null);

        User user5 = new User();
        user5.setGender("male");
        user5.setBachelor(false);
        user5.setGradePast(null);

        List<User> users = Arrays.asList(user1, user2, user3);
        List<User> teachers = Arrays.asList(user4, user5);
        String[] nameOfTeams = {"Team 1", "Team 2"};
        int numberOfTeams = 2;
        int numberOfGirlsPerTeam = 1;

        // Define the behavior of userService and teamDao
        when(userService.existsByEmail(anyString())).thenReturn(true);
        when(userService.loadUserByEmail(anyString())).thenReturn(new User());
        when(teamDao.save(any(Team.class))).thenAnswer(i -> i.getArguments()[0]);
        when(criteriaService.save(any(Criteria.class))).thenAnswer(i -> i.getArguments()[0]);

        // Call the method under test
        List<Team> result = teamService.createTeams(users, teachers, nameOfTeams, numberOfTeams, numberOfGirlsPerTeam);

        // Verify the result
        assertEquals(numberOfTeams, result.size());
    }

    @Test
    void testCreateTeamsEmptyUsersOrTeachers() {
        List<User> users = new ArrayList<>();
        List<User> teachers = new ArrayList<>();
        String[] nameOfTeams = {"Team 1", "Team 2"};
        int numberOfTeams = 2;
        int numberOfGirlsPerTeam = 1;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> teamService.createTeams(users, teachers, nameOfTeams, numberOfTeams, numberOfGirlsPerTeam));

        String expectedMessage = "Not enough users or teachers to create the teams";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCreateTeamsNotEnoughUsers() {
        User user = new User();
        User teacher = new User();
        List<User> users = Collections.singletonList(user);
        List<User> teachers = Arrays.asList(teacher, teacher);
        String[] nameOfTeams = {"Team 1", "Team 2"};
        int numberOfTeams = 2;
        int numberOfGirlsPerTeam = 1;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> teamService.createTeams(users, teachers, nameOfTeams, numberOfTeams, numberOfGirlsPerTeam));

        String expectedMessage = "Not enough users to create the teams";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCreateTeamsNotEnoughTeachers() {
        User user = new User();
        User teacher = new User();
        List<User> users = Arrays.asList(user, user);
        List<User> teachers = Collections.singletonList(teacher);
        String[] nameOfTeams = {"Team 1", "Team 2"};
        int numberOfTeams = 2;
        int numberOfGirlsPerTeam = 1;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> teamService.createTeams(users, teachers, nameOfTeams, numberOfTeams, numberOfGirlsPerTeam));

        String expectedMessage = "Not enough teachers to create the teams";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCreateTeamsEmptyUsers() {
        List<User> users = new ArrayList<>();
        List<User> teachers = Arrays.asList(new User(), new User());
        String[] nameOfTeams = {"Team 1", "Team 2"};
        int numberOfTeams = 2;
        int numberOfGirlsPerTeam = 1;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> teamService.createTeams(users, teachers, nameOfTeams, numberOfTeams, numberOfGirlsPerTeam));

        String expectedMessage = "Not enough users or teachers to create the teams";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCreateTeamsNotEnoughNames() {
        User user1 = new User();
        user1.setGender("male");
        user1.setBachelor(true);
        user1.setGradePast(95.0);

        User user2 = new User();
        user2.setGender("female");
        user2.setBachelor(false);
        user2.setGradePast(90.0);

        User user3 = new User();
        user3.setGender("male");
        user3.setBachelor(false);
        user3.setGradePast(88.0);

        User user4 = new User();
        user4.setGender("female");
        user4.setBachelor(false);
        user4.setGradePast(90.0);
        User teacher = new User();
        userService.saveUser(user1);
        userService.saveUser(user2);
        userService.saveUser(user3);
        userService.saveUser(user4);
        List<User> users = Arrays.asList(user1, user2, user3, user4);
        List<User> teachers = Arrays.asList(teacher, teacher);
        String[] nameOfTeams = {"Team 1"};
        int numberOfTeams = 2;
        int numberOfGirlsPerTeam = 1;

        List<Team> teams = teamService.createTeams(users, teachers, nameOfTeams, numberOfTeams, numberOfGirlsPerTeam);

        assertEquals(numberOfTeams, teams.size());
        assertEquals("Team 1", teams.get(0).getName());
        assertEquals("Team 2", teams.get(1).getName());
    }

    @Test
    void testCreateTeamsUserExists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setGender("female");
        user.setGradePast(90.0);
        User teacher = new User();
        List<User> users = Collections.singletonList(user);
        List<User> teachers = Arrays.asList(teacher, teacher);
        String[] nameOfTeams = {"Team 1", "Team 2"};
        int numberOfTeams = 1;
        int numberOfGirlsPerTeam = 1;

        when(userService.existsByEmail(user.getEmail())).thenReturn(true);
        when(userService.loadUserByEmail(user.getEmail())).thenReturn(user);

        List<Team> teams = teamService.createTeams(users, teachers, nameOfTeams, numberOfTeams, numberOfGirlsPerTeam);

        assertEquals(numberOfTeams, teams.size());
        verify(userService, times(1)).existsByEmail(user.getEmail());
        verify(userService, times(1)).loadUserByEmail(user.getEmail());
    }

    @Test
    void testAddTeamMember() {
        // Prepare test data
        Team team = new Team();
        team.setId(1);
        team.setName("Test Team");

        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");

        // Define the behavior of teamDao and userDao
        when(teamDao.findById(team.getId())).thenReturn(Optional.of(team));
        when(userService.getUserById(user.getId())).thenReturn(user);

        // Call the method under test
        User addedUser = teamService.addTeamMember(team.getId(), user.getId());

        // Verify the result
        assertNotNull(addedUser);
        assertEquals(user.getId(), addedUser.getId());
        assertTrue(team.getUsers().contains(addedUser));
    }

    @Test
    void testGetTeamMembers() {
        // Prepare test data
        Team team = new Team();
        team.setId(1);
        team.setName("Test Team");

        User user1 = new User();
        user1.setId(1);
        user1.setEmail("test1@example.com");

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("test2@example.com");

        // Define the behavior of teamDao and userService
        when(teamDao.findById(team.getId())).thenReturn(Optional.of(team));
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(userService.getUserById(user2.getId())).thenReturn(user2);
        when(userService.findByTeam(team.getId())).thenReturn(Arrays.asList(user1, user2));

        // Add users to the team
        teamService.addTeamMember(team.getId(), user1.getId());
        teamService.addTeamMember(team.getId(), user2.getId());

        // Call the method under test
        List<User> teamMembers = teamService.getTeamMembers(team.getId());

        // Verify the result
        assertEquals(2, teamMembers.size());
        assertTrue(teamMembers.contains(user1));
        assertTrue(teamMembers.contains(user2));
    }

    @Test
    void testGetTeamByUserId_Success() {
        // Prepare test data
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");

        Team team = new Team();
        team.setId(1);
        team.setName("Test Team");

        UserTeam userTeam = new UserTeam(user, team);

        // Define the behavior of userService and userTeamDao
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(userTeamDao.findByUser(user)).thenReturn(Optional.of(userTeam));

        // Call the method under test
        Team result = teamService.getTeamByUserId(user.getId());

        // Verify the result
        assertNotNull(result);
        assertEquals(team.getId(), result.getId());
        assertEquals(team.getName(), result.getName());
    }

    @Test
    void testGetTeamByUserId_Exception() {
        // Prepare test data
        int userId = 1;
        User user = new User();
        user.setId(userId);

        // Define the behavior of userService and userTeamDao
        when(userService.getUserById(userId)).thenReturn(user);
        when(userTeamDao.findByUser(user)).thenThrow(new RuntimeException("Error occurred while fetching team by user id: " + userId));

        // Verify that an exception is thrown
        Exception exception = assertThrows(RuntimeException.class, () -> teamService.getTeamByUserId(userId));

        String expectedMessage = "Failed to get team by user id: " + userId;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testGetOneUserInTeam_UserTeamOptionalIsPresent() {
        int userId = 1;
        int teamId = 1;

        User expectedUser = new User();
        expectedUser.setId(userId);

        Team team = new Team();
        team.setId(teamId);

        UserTeam userTeam = new UserTeam(expectedUser, team);

        when(teamDao.findById(teamId)).thenReturn(Optional.of(team));
        when(userService.getUserById(userId)).thenReturn(expectedUser);
        when(userTeamDao.findByUserAndTeam(expectedUser, team)).thenReturn(Optional.of(userTeam));

        User actualUser = teamService.getOneUserInTeam(userId, teamId);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void testGetOneUserInTeam_UserTeamOptionalIsNotPresent() {
        int userId = 1;
        int teamId = 1;

        User user = new User();
        user.setId(userId);

        Team team = new Team();
        team.setId(teamId);

        when(teamDao.findById(teamId)).thenReturn(Optional.of(team));
        when(userService.getUserById(userId)).thenReturn(user);
        when(userTeamDao.findByUserAndTeam(user, team)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> teamService.getOneUserInTeam(userId, teamId));

        String expectedMessage = "User with id " + userId + " is not in team with id " + teamId;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getOneUserInTeamShouldThrowEntityNotFoundExceptionWhenTeamIsNull() {
        int userId = 1;
        int teamId = 1;

        User user = mock(User.class);
        when(userService.getUserById(userId)).thenReturn(user);
        when(teamDao.findById(teamId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> teamService.getOneUserInTeam(userId, teamId));

        String expectedMessage = "Team with id " + teamId + " doesn't exist.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateTeam() {
        // Prepare test data
        User supervisor = new User();
        supervisor.setId(1);

        User teamMember = new User();
        teamMember.setId(2);

        UserInteract supervisorDTO = new UserInteract();
        supervisorDTO.setId(supervisor.getId());

        UserInteract teamMemberDTO = new UserInteract();
        teamMemberDTO.setId(teamMember.getId());

        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setId(1);
        teamDTO.setName("Test Team");
        teamDTO.setStatus("none");
        teamDTO.setSupervisor(supervisorDTO);
        teamDTO.setUsers(Collections.singletonList(teamMemberDTO));

        Team team = new Team();
        team.setId(teamDTO.getId());
        team.setName(teamDTO.getName());
        team.setStatus(teamDTO.getStatus());

        // Define the behavior of teamDao and userService
        when(teamDao.findById(teamDTO.getId())).thenReturn(Optional.of(team));
        when(userService.getUserById(supervisorDTO.getId())).thenReturn(supervisor);
        when(userService.getUserById(teamMemberDTO.getId())).thenReturn(teamMember);
        when(teamDao.save(any(Team.class))).thenReturn(team);

        // Call the method under test
        teamService.updateTeam(teamDTO);

        // Verify the interactions
        verify(teamDao, times(3)).save(any(Team.class));
        verify(userService, times(2)).updateUser(any(User.class));
    }

    @Test
    void testDeleteTeam() {
        // Prepare test data
        int teamId = 1;
        Team team = new Team();
        team.setId(teamId);

        // Define the behavior of teamDao
        when(teamDao.findById(teamId)).thenReturn(Optional.of(team));
        doNothing().when(teamDao).delete(team);

        // Call the method under test
        teamService.deleteTeam(teamId);

        // Verify the interactions
        verify(teamDao, times(1)).delete(team);
    }

    @Test
    void testDeleteAllTeam() {
        // Define the behavior of all Dao and Service
        doNothing().when(cleanupService).deleteAllTeam();

        // Call the method under test
        teamService.deleteAllTeam();

        // Verify the interactions
        verify(cleanupService, times(1)).deleteAllTeam();
    }

    @Test
    void testToDTO() {
        // Prepare test data
        Team team = new Team();
        team.setId(1);
        team.setName("Test Team");

        // Call the method under test
        TeamSendDTO teamDTO = teamService.toDTO(team);

        // Verify the result
        assertNotNull(teamDTO);
        assertEquals(team.getId(), teamDTO.getId());
        assertEquals(team.getName(), teamDTO.getName());
    }

}
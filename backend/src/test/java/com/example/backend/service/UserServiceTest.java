package com.example.backend.service;

import com.example.backend.dao.RoleDao;
import com.example.backend.dao.TeamDao;
import com.example.backend.dao.UserDao;
import com.example.backend.dto.UserInteract;
import com.example.backend.dto.UserSendDTO;
import com.example.backend.model.Role;
import com.example.backend.model.Team;
import com.example.backend.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CleanupService cleanupService;

    @Mock
    private TeamDao teamDao;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        User user2 = new User();
        when(userDao.findAll()).thenReturn(Arrays.asList(user1, user2));

        assertEquals(2, userService.getAllUsers().size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1);
        when(userDao.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);
        assertEquals(1, result.getId());
        verify(userDao, times(1)).findById(1);
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setId(1);
        when(userDao.findById(1)).thenReturn(Optional.of(user));
        doNothing().when(notificationService).deleteAllNotificationsOfUser(1);

        userService.deleteUser(1);
        verify(userDao, times(1)).deleteById(1);
    }

    @Test
    void testGetTeachers() {
        Role ssRole = new Role();
        ssRole.setName(Role.RoleName.SS);
        User user1 = new User();
        User user2 = new User();
        when(roleDao.findByName(Role.RoleName.SS)).thenReturn(Optional.of(ssRole));
        when(userDao.findByRoles(ssRole)).thenReturn(Optional.of(Arrays.asList(user1, user2)));

        assertEquals(2, userService.getTeachers().size());
        verify(userDao, times(1)).findByRoles(ssRole);
    }

    @Test
    void testGetStudents() {
        Role osRole = new Role();
        osRole.setName(Role.RoleName.OS);
        User user1 = new User();
        User user2 = new User();
        when(roleDao.findByName(Role.RoleName.OS)).thenReturn(Optional.of(osRole));
        when(userDao.findByRoles(osRole)).thenReturn(Optional.of(Arrays.asList(user1, user2)));

        assertEquals(2, userService.getStudents().size());
        verify(userDao, times(1)).findByRoles(osRole);
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userDao.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userDao.save(user)).thenReturn(user);

        User result = userService.saveUser(user);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userDao, times(1)).save(user);
    }

    @Test
    void testSaveUserWithExistingEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userDao.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(Exception.class, () -> userService.saveUser(user));

        String expectedMessage = "Email is already in use";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(userDao, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setId(1);
        when(userDao.save(user)).thenReturn(user);

        User result = userService.updateUser(user);
        assertEquals(user.getId(), result.getId());
        verify(userDao, times(1)).save(user);
    }

    @Test
    void testExistsByEmail() {
        String email = "test@example.com";
        when(userDao.findByEmail(email)).thenReturn(Optional.of(new User()));

        boolean exists = userService.existsByEmail(email);
        Assertions.assertTrue(exists);
        verify(userDao, times(1)).findByEmail(email);
    }

    @Test
    void testFindByTeam() {
        int teamId = 1;
        User user1 = new User();
        User user2 = new User();
        when(userDao.findByTeams_Id(teamId)).thenReturn(Arrays.asList(user1, user2));

        assertEquals(2, userService.findByTeam(teamId).size());
        verify(userDao, times(1)).findByTeams_Id(teamId);
    }

    @Test
    void testSetDefaultRoles() {
        User user = new User();
        Role role = new Role();
        role.setName(Role.RoleName.OS);
        when(roleDao.findByName(Role.RoleName.OS)).thenReturn(Optional.of(role));

        userService.setDefaultRoles(user);
        Assertions.assertTrue(user.getRoles().contains(role));
    }

    @Test
    void testDeleteStudents() {
        // Define the behavior of all Dao and Service
        doNothing().when(cleanupService).deleteAllStudents();

        // Call the method under test
        userService.deleteStudents();

        // Verify the interactions
        verify(cleanupService, times(1)).deleteAllStudents();
    }

    @Test
    void testLoadUserByUsername() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        when(userDao.findByEmail(email)).thenReturn(Optional.of(user));

        assertEquals(email, user.getEmail());

        UserDetails userDetails = userService.loadUserByUsername(email);
        assertNotNull(userDetails);
        verify(userDao, times(1)).findByEmail(email);
    }

    @Test
    void testLoadUserByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userDao.findByEmail(email)).thenReturn(Optional.of(user));

        User returnedUser = userService.loadUserByEmail(email);

        assertNotNull(returnedUser);
        assertEquals(email, returnedUser.getEmail());
        verify(userDao, times(1)).findByEmail(email);
    }

    @Test
    void testGetUserByIdNotOptional() {
        int userId = 1;
        User user = new User();
        user.setId(userId);
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        User returnedUser = userService.getUserById(userId);

        assertNotNull(returnedUser);
        assertEquals(userId, returnedUser.getId());
        verify(userDao, times(1)).findById(userId);
    }

    @Test
    void testUserInteractToUserNewUser() {
        UserInteract userDto = new UserInteract();
        userDto.setEmail("test@example.com");
        when(userDao.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());

        Optional<User> returnedUser = userService.userInteractToUser(userDto);

        assertTrue(returnedUser.isPresent());
        assertEquals(userDto.getEmail(), returnedUser.get().getEmail());
        verify(userDao, times(1)).findByEmail(userDto.getEmail());
    }

    @Test
    void testUserInteractToUserExistingUser() {
        UserInteract userDto = new UserInteract();
        userDto.setEmail("test@example.com");
        User existingUser = new User();
        existingUser.setEmail(userDto.getEmail());
        when(userDao.findByEmail(userDto.getEmail())).thenReturn(Optional.of(existingUser));

        Optional<User> returnedUser = userService.userInteractToUser(userDto);

        assertTrue(returnedUser.isPresent());
        assertEquals(userDto.getEmail(), returnedUser.get().getEmail());
        verify(userDao, times(2)).findByEmail(userDto.getEmail());
    }

    @Test
    void testToDTO() {
        // Prepare test data
        User user = new User();
        user.setId(1);
        user.setFirstName("Test");
        user.setLastName("User");
        Role osRole = new Role();
        osRole.setName(Role.RoleName.OS);
        user.setRoles(new HashSet<>(List.of(osRole)));

        // Call the method under test
        UserSendDTO userDTO = userService.toDTO(user);

        // Verify the result
        assertNotNull(userDTO);
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getLastName(), userDTO.getLastName());
        assertTrue(userDTO.getRoles().contains(osRole.getName().toString()));
    }

    @Test
    void getTechnicalCoachesShouldReturnListOfTechnicalCoaches() {
        Role tcRole = new Role();
        tcRole.setName(Role.RoleName.TC);
        User user1 = new User();
        User user2 = new User();
        when(roleDao.findByName(Role.RoleName.TC)).thenReturn(Optional.of(tcRole));
        when(userDao.findByRoles(tcRole)).thenReturn(Optional.of(Arrays.asList(user1, user2)));

        List<User> result = userService.getTechnicalCoaches();

        assertEquals(2, result.size());
        verify(userDao, times(1)).findByRoles(tcRole);
    }

    @Test
    void getTechnicalCoachesShouldThrowRuntimeExceptionWhenRoleNotFound() {
        when(roleDao.findByName(Role.RoleName.TC)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getTechnicalCoaches());
    }

    @Test
    void getProjectLeaderShouldReturnListOfProjectLeaders() {
        Role plRole = new Role();
        plRole.setName(Role.RoleName.PL);
        User user1 = new User();
        User user2 = new User();
        when(roleDao.findByName(Role.RoleName.PL)).thenReturn(Optional.of(plRole));
        when(userDao.findByRoles(plRole)).thenReturn(Optional.of(Arrays.asList(user1, user2)));

        List<User> result = userService.getProjectLeader();

        assertEquals(2, result.size());
        verify(userDao, times(1)).findByRoles(plRole);
    }

    @Test
    void getProjectLeaderShouldThrowRuntimeExceptionWhenRoleNotFound() {
        when(roleDao.findByName(Role.RoleName.PL)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getProjectLeader());
    }

    @Test
    void getOptionLeaderShouldReturnListOfOptionLeaders() {
        Role olRole = new Role();
        olRole.setName(Role.RoleName.OL);
        User user1 = new User();
        User user2 = new User();
        when(roleDao.findByName(Role.RoleName.OL)).thenReturn(Optional.of(olRole));
        when(userDao.findByRoles(olRole)).thenReturn(Optional.of(Arrays.asList(user1, user2)));

        List<User> result = userService.getOptionLeader();

        assertEquals(2, result.size());
        verify(userDao, times(1)).findByRoles(olRole);
    }

    @Test
    void getOptionLeaderShouldThrowRuntimeExceptionWhenRoleNotFound() {
        when(roleDao.findByName(Role.RoleName.OL)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getOptionLeader());
    }

    @Test
    void getAllTeachersShouldReturnAllTeachers() {
        Role ssRole = new Role();
        ssRole.setName(Role.RoleName.SS);
        Role tcRole = new Role();
        tcRole.setName(Role.RoleName.TC);
        Role plRole = new Role();
        plRole.setName(Role.RoleName.PL);
        Role olRole = new Role();
        olRole.setName(Role.RoleName.OL);
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        User user4 = new User();
        when(roleDao.findByName(Role.RoleName.SS)).thenReturn(Optional.of(ssRole));
        when(roleDao.findByName(Role.RoleName.TC)).thenReturn(Optional.of(tcRole));
        when(roleDao.findByName(Role.RoleName.PL)).thenReturn(Optional.of(plRole));
        when(roleDao.findByName(Role.RoleName.OL)).thenReturn(Optional.of(olRole));
        when(userDao.findByRoles(ssRole)).thenReturn(Optional.of(List.of(user1)));
        when(userDao.findByRoles(tcRole)).thenReturn(Optional.of(List.of(user2)));
        when(userDao.findByRoles(plRole)).thenReturn(Optional.of(List.of(user3)));
        when(userDao.findByRoles(olRole)).thenReturn(Optional.of(List.of(user4)));

        List<User> result = userService.getAllTeachers();

        assertEquals(4, result.size());
        assertTrue(result.containsAll(Arrays.asList(user1, user2, user3, user4)));
    }

    @Test
    void getTeachersWithTeamsShouldReturnListOfTeachersWithTeams() {
        Team team1 = mock(Team.class);
        User user1 = mock(User.class);
        when(team1.getSupervisor()).thenReturn(user1);
        Team team2 = mock(Team.class);
        User user2 = mock(User.class);
        when(team2.getSupervisor()).thenReturn(user2);
        List<Team> teams = Arrays.asList(team1, team2);
        when(teamDao.findAll()).thenReturn(teams);

        List<User> result = userService.getTeachersWithTeams();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(user1, user2)));
        verify(teamDao, times(1)).findAll();
        verify(team1, times(1)).getSupervisor();
        verify(team2, times(1)).getSupervisor();
    }

    @Test
    void getAllStaffShouldReturnAllStaff() {
        Role ssRole = new Role();
        ssRole.setName(Role.RoleName.SS);
        Role plRole = new Role();
        plRole.setName(Role.RoleName.PL);
        User user1 = new User();
        User user2 = new User();
        when(roleDao.findByName(Role.RoleName.SS)).thenReturn(Optional.of(ssRole));
        when(roleDao.findByName(Role.RoleName.PL)).thenReturn(Optional.of(plRole));
        when(userDao.findByRolesIn(Arrays.asList(ssRole, plRole))).thenReturn(Arrays.asList(user1, user2));

        List<User> result = userService.getAllStaff();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(user1, user2)));
        verify(roleDao, times(1)).findByName(Role.RoleName.SS);
        verify(roleDao, times(1)).findByName(Role.RoleName.PL);
        verify(userDao, times(1)).findByRolesIn(Arrays.asList(ssRole, plRole));
    }

    @Test
    void getAllStaffShouldThrowRuntimeExceptionWhenNoUsersFound() {
        Role ssRole = new Role();
        ssRole.setName(Role.RoleName.SS);
        Role plRole = new Role();
        plRole.setName(Role.RoleName.PL);
        when(roleDao.findByName(Role.RoleName.SS)).thenReturn(Optional.of(ssRole));
        when(roleDao.findByName(Role.RoleName.PL)).thenReturn(Optional.of(plRole));
        when(userDao.findByRolesIn(Arrays.asList(ssRole, plRole))).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.getAllStaff());

        String expectedMessage = "No users found with roles SS or PL";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getUserByIdNotOptionalShouldReturnUserWhenUserExists() {
        int userId = 1;
        User expectedUser = new User();
        when(userDao.findById(userId)).thenReturn(Optional.of(expectedUser));

        User result = userService.getUserByIdNotOptional(userId);

        assertEquals(expectedUser, result);
        verify(userDao, times(1)).findById(userId);
    }

    @Test
    void getUserByIdNotOptionalShouldReturnNullWhenUserDoesNotExist() {
        int userId = 1;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        User result = userService.getUserByIdNotOptional(userId);

        assertNull(result);
        verify(userDao, times(1)).findById(userId);
    }

    @Test
    void getRoleByNameShouldReturnRoleWhenRoleExists() {
        String roleName = "SS";
        Role expectedRole = new Role();
        expectedRole.setName(Role.RoleName.valueOf(roleName));
        when(roleDao.findByName(Role.RoleName.valueOf(roleName))).thenReturn(Optional.of(expectedRole));

        Role result = userService.getRoleByName(roleName);

        assertEquals(expectedRole, result);
        verify(roleDao, times(1)).findByName(Role.RoleName.valueOf(roleName));
    }

    @Test
    void getRoleByNameShouldThrowIllegalArgumentExceptionWhenRoleDoesNotExist() {
        String roleName = "SS";
        when(roleDao.findByName(Role.RoleName.valueOf(roleName))).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.getRoleByName(roleName));

        String expectedMessage = "Role not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
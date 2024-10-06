package com.example.backend.service;

import com.example.backend.dao.BMValidationDao;
import com.example.backend.dao.BonusMalusDao;
import com.example.backend.dto.BonusMalusDTO;
import com.example.backend.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BonusMalusServiceTest {

    @Mock
    private BonusMalusDao bonusMalusDao;

    @Mock
    private BMValidationDao bmValidationDao;

    @Mock
    private TeamService teamService;

    @Mock
    private SprintService sprintService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BonusMalusService bonusMalusService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBonusMalus() {
        // Arrange
        User user1 = new User();
        user1.setId(1);

        BonusMalus bonusMalus1 = new BonusMalus();
        bonusMalus1.setId(1);
        bonusMalus1.setValue(10);
        bonusMalus1.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus1.setAttributedBy(user1);
        bonusMalus1.setAttributedTo(user1);
        Team team1 = new Team();
        team1.setId(1);
        bonusMalus1.setTeam(team1);
        Sprint sprint1 = new Sprint();
        sprint1.setId(1);
        bonusMalus1.setSprint(sprint1);

        User user2 = new User();
        user2.setId(2);

        BonusMalus bonusMalus2 = new BonusMalus();
        bonusMalus2.setId(2);
        bonusMalus2.setValue(20);
        bonusMalus2.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus2.setAttributedBy(user2);
        bonusMalus2.setAttributedTo(user2);
        Team team2 = new Team();
        team2.setId(2);
        bonusMalus2.setTeam(team2);
        Sprint sprint2 = new Sprint();
        sprint2.setId(2);
        bonusMalus2.setSprint(sprint2);

        List<BonusMalus> bonusMalusList = Arrays.asList(bonusMalus1, bonusMalus2);

        when(bonusMalusDao.findAllBySprintId(anyInt())).thenReturn(bonusMalusList);

        // Act
        List<List<BonusMalusDTO>> result = bonusMalusService.getAllBonusMalus(1);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).size());
        assertEquals(1, result.get(0).get(0).getId());
        assertEquals(10, result.get(0).get(0).getValue());
        assertEquals(1, result.get(0).get(0).getTeamId());
        assertEquals(BonusMalus.BonusMalusStatus.PENDING.toString(), result.get(0).get(0).getStatus());

        assertEquals(1, result.get(1).size());
        assertEquals(2, result.get(1).get(0).getId());
        assertEquals(20, result.get(1).get(0).getValue());
        assertEquals(2, result.get(1).get(0).getTeamId());
        assertEquals(BonusMalus.BonusMalusStatus.PENDING.toString(), result.get(1).get(0).getStatus());
    }

    @Test
    void testGetBmByStudentAndSprint() {
        // Arrange
        User student = new User();
        student.setId(1);

        Sprint sprint = new Sprint();
        sprint.setId(1);

        BonusMalus expectedBonusMalus = new BonusMalus();
        expectedBonusMalus.setId(1);
        expectedBonusMalus.setAttributedTo(student);
        expectedBonusMalus.setSprint(sprint);
        expectedBonusMalus.setTeam(new Team());
        expectedBonusMalus.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        expectedBonusMalus.setAttributedTo(student);
        expectedBonusMalus.setAttributedBy(student);

        List<BonusMalus> bonusMalusList = Collections.singletonList(expectedBonusMalus);

        when(bonusMalusDao.findAllByTeamIdAndSprintId(student.getId(), sprint.getId())).thenReturn(bonusMalusList);

        // Act
        List<BonusMalusDTO> result = bonusMalusService.getLBmByTeamAndSprint(student.getId(), sprint.getId());

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testGetBmByTeamAndSprint_SS() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setRoles(Collections.singleton(new Role(Role.RoleName.SS)));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        Team team = new Team();
        team.setId(1);

        Sprint sprint = new Sprint();
        sprint.setId(1);

        BonusMalus bonusMalus1 = new BonusMalus();
        bonusMalus1.setId(1);
        bonusMalus1.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus1.setAttributedTo(user);
        bonusMalus1.setAttributedBy(user);
        bonusMalus1.setTeam(team);
        bonusMalus1.setSprint(sprint);
        bonusMalus1.setUnlimited(true);

        BonusMalus bonusMalus2 = new BonusMalus();
        bonusMalus2.setId(2);
        bonusMalus2.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus2.setAttributedTo(user);
        bonusMalus2.setAttributedBy(user);
        bonusMalus2.setTeam(team);
        bonusMalus2.setSprint(sprint);
        bonusMalus2.setUnlimited(true);

        List<BonusMalus> expectedBonusMalusList = Arrays.asList(bonusMalus1, bonusMalus2);

        when(bonusMalusDao.findAllByTeamIdAndSprintId(team.getId(), sprint.getId())).thenReturn(expectedBonusMalusList);

        bonusMalusService.getLBmByTeamAndSprint(team.getId(), sprint.getId());

        verify(bonusMalusDao, times(1)).findAllByTeamIdAndSprintId(team.getId(), sprint.getId());
    }

    @Test
    void testGetBmByTeamAndSprint_notSS() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setRoles(Collections.singleton(new Role(Role.RoleName.PL)));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        Team team = new Team();
        team.setId(1);

        Sprint sprint = new Sprint();
        sprint.setId(1);

        BonusMalus bonusMalus1 = new BonusMalus();
        bonusMalus1.setId(1);
        bonusMalus1.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus1.setAttributedTo(user);
        bonusMalus1.setAttributedBy(user);
        bonusMalus1.setTeam(team);
        bonusMalus1.setSprint(sprint);
        bonusMalus1.setUnlimited(false);

        BonusMalus bonusMalus2 = new BonusMalus();
        bonusMalus2.setId(2);
        bonusMalus2.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus2.setAttributedTo(user);
        bonusMalus2.setAttributedBy(user);
        bonusMalus2.setTeam(team);
        bonusMalus2.setSprint(sprint);
        bonusMalus2.setUnlimited(false);

        List<BonusMalus> expectedBonusMalusList = Arrays.asList(bonusMalus1, bonusMalus2);

        when(bonusMalusDao.findAllByTeamIdAndSprintId(team.getId(), sprint.getId())).thenReturn(expectedBonusMalusList);

        bonusMalusService.getLBmByTeamAndSprint(team.getId(), sprint.getId());

        verify(bonusMalusDao, times(1)).findAllByTeamIdAndSprintId(team.getId(), sprint.getId());
    }

    @Test
    void testAddBonusMalusByTeam_SS(){
        // Arrange
        User user = new User();
        user.setId(1);
        user.setRoles(Collections.singleton(new Role(Role.RoleName.SS)));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        Team team = new Team();
        team.setId(1);

        Sprint sprint = new Sprint();
        sprint.setId(1);

        when(teamService.getTeamById(team.getId())).thenReturn(team);
        when(sprintService.getSprintById(sprint.getId())).thenReturn(new Sprint());

        BonusMalusDTO bonusMalusDTO = new BonusMalusDTO();
        bonusMalusDTO.setId(1);
        bonusMalusDTO.setAttributedTo(user.getId());
        bonusMalusDTO.setAttributedBy(user.getId());
        bonusMalusDTO.setTeamId(team.getId());
        bonusMalusDTO.setSprintId(sprint.getId());
        bonusMalusDTO.setUnlimited(true);

        BonusMalus bonusMalus1 = new BonusMalus();
        bonusMalus1.setId(1);
        bonusMalus1.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus1.setAttributedTo(user);
        bonusMalus1.setAttributedBy(user);
        bonusMalus1.setTeam(team);
        bonusMalus1.setSprint(sprint);
        bonusMalus1.setUnlimited(true);

        when(teamService.getOneUserInTeam(bonusMalusDTO.getAttributedTo(), team.getId())).thenReturn(user);

        List<BonusMalusDTO> bonusMalusDTOList = List.of(bonusMalusDTO);

        when(bonusMalusDao.save(any(BonusMalus.class))).thenReturn(bonusMalus1);

        // Act
        List<BonusMalusDTO> result = bonusMalusService.addBonusMalusByTeam(team.getId(), sprint.getId(), bonusMalusDTOList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bonusMalusDTO.getId(), result.get(0).getId());
    }

    @Test
    void testAddBonusMalusByTeam_SS_TeamNotFound() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setRoles(Collections.singleton(new Role(Role.RoleName.SS)));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        int teamId = 1;
        int sprintId = 1;

        when(teamService.getTeamById(teamId)).thenReturn(null);

        BonusMalusDTO bonusMalusDTO = new BonusMalusDTO();
        bonusMalusDTO.setId(1);
        bonusMalusDTO.setAttributedTo(user.getId());
        bonusMalusDTO.setAttributedBy(user.getId());
        bonusMalusDTO.setTeamId(teamId);
        bonusMalusDTO.setSprintId(sprintId);
        bonusMalusDTO.setUnlimited(true);

        List<BonusMalusDTO> bonusMalusDTOList = List.of(bonusMalusDTO);

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> bonusMalusService.addBonusMalusByTeam(teamId, sprintId, bonusMalusDTOList));

        String expectedMessage = "Team with id " + teamId + " doesn't exist.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddBonusMalusByTeam_OS_TotalValueZero() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setRoles(Collections.singleton(new Role(Role.RoleName.OS)));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        Team team = new Team();
        team.setId(1);
        User supervisor = new User();
        supervisor.setId(2);
        team.setSupervisor(supervisor);

        Sprint sprint = new Sprint();
        sprint.setId(1);

        Notification notification = new Notification();
        notification.setId(1);
        notification.setEmitter(team.getSupervisor());

        when(teamService.getTeamByUserId(user.getId())).thenReturn(team);
        when(sprintService.getSprintById(sprint.getId())).thenReturn(new Sprint());
        when(teamService.getTeamById(team.getId())).thenReturn(team);
        when(teamService.getOneUserInTeam(user.getId(), team.getId())).thenReturn(user);
        when(teamService.getOneUserInTeam(supervisor.getId(), team.getId())).thenReturn(supervisor);
        when(teamService.getOneUserInTeam(notification.getEmitter().getId(), team.getId())).thenReturn(notification.getEmitter());
        when(teamService.getTeamMembers(team.getId())).thenReturn(List.of(user, supervisor, notification.getEmitter()));
        when(notificationService.createNotification(any(Notification.class))).thenReturn(notification);

        BonusMalusDTO bonusMalusDTO1 = new BonusMalusDTO();
        bonusMalusDTO1.setId(1);
        bonusMalusDTO1.setAttributedTo(user.getId());
        bonusMalusDTO1.setAttributedBy(user.getId());
        bonusMalusDTO1.setTeamId(team.getId());
        bonusMalusDTO1.setSprintId(sprint.getId());
        bonusMalusDTO1.setUnlimited(false);
        bonusMalusDTO1.setValue(4);

        BonusMalusDTO bonusMalusDTO2 = new BonusMalusDTO();
        bonusMalusDTO2.setId(2);
        bonusMalusDTO2.setAttributedTo(user.getId());
        bonusMalusDTO2.setAttributedBy(user.getId());
        bonusMalusDTO2.setTeamId(team.getId());
        bonusMalusDTO2.setSprintId(sprint.getId());
        bonusMalusDTO2.setUnlimited(false);
        bonusMalusDTO2.setValue(-4);

        List<BonusMalusDTO> bonusMalusDTOList = List.of(bonusMalusDTO1, bonusMalusDTO2);

        BonusMalus bonusMalus1 = new BonusMalus();
        bonusMalus1.setId(1);
        bonusMalus1.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus1.setAttributedTo(user);
        bonusMalus1.setAttributedBy(user);
        bonusMalus1.setTeam(team);
        bonusMalus1.setSprint(sprint);
        bonusMalus1.setUnlimited(false);

        BonusMalus bonusMalus2 = new BonusMalus();
        bonusMalus2.setId(1);
        bonusMalus2.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus2.setAttributedTo(user);
        bonusMalus2.setAttributedBy(user);
        bonusMalus2.setTeam(team);
        bonusMalus2.setSprint(sprint);
        bonusMalus2.setUnlimited(false);

        when(bonusMalusDao.saveAll(anyList())).thenReturn(new ArrayList<>(List.of(bonusMalus1, bonusMalus2)));

        // Act
        List<BonusMalusDTO> result = bonusMalusService.addBonusMalusByTeam(team.getId(), sprint.getId(), bonusMalusDTOList);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testAddBonusMalusByTeam_OS_TotalValueNotZero() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setRoles(Collections.singleton(new Role(Role.RoleName.OS)));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        Team team = new Team();
        team.setId(1);

        Sprint sprint = new Sprint();
        sprint.setId(1);

        when(teamService.getTeamByUserId(user.getId())).thenReturn(team);
        when(sprintService.getSprintById(sprint.getId())).thenReturn(new Sprint());

        BonusMalusDTO bonusMalusDTO1 = new BonusMalusDTO();
        bonusMalusDTO1.setId(1);
        bonusMalusDTO1.setAttributedTo(user.getId());
        bonusMalusDTO1.setAttributedBy(user.getId());
        bonusMalusDTO1.setTeamId(team.getId());
        bonusMalusDTO1.setSprintId(sprint.getId());
        bonusMalusDTO1.setUnlimited(false);
        bonusMalusDTO1.setValue(10);

        BonusMalusDTO bonusMalusDTO2 = new BonusMalusDTO();
        bonusMalusDTO2.setId(2);
        bonusMalusDTO2.setAttributedTo(user.getId());
        bonusMalusDTO2.setAttributedBy(user.getId());
        bonusMalusDTO2.setTeamId(team.getId());
        bonusMalusDTO2.setSprintId(sprint.getId());
        bonusMalusDTO2.setUnlimited(false);
        bonusMalusDTO2.setValue(10);

        List<BonusMalusDTO> bonusMalusDTOList = List.of(bonusMalusDTO1, bonusMalusDTO2);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> bonusMalusService.addBonusMalusByTeam(team.getId(), sprint.getId(), bonusMalusDTOList));

        String expectedMessage = "The total value of bonus/malus must be zero for non-SS users.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddBonusMalusByTeam_OS_TeamNotFound() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setRoles(Collections.singleton(new Role(Role.RoleName.OS)));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        int teamId = 1;
        int sprintId = 1;

        when(teamService.getTeamById(teamId)).thenReturn(null);

        BonusMalusDTO bonusMalusDTO = new BonusMalusDTO();
        bonusMalusDTO.setId(1);
        bonusMalusDTO.setAttributedTo(user.getId());
        bonusMalusDTO.setAttributedBy(user.getId());
        bonusMalusDTO.setTeamId(teamId);
        bonusMalusDTO.setSprintId(sprintId);
        bonusMalusDTO.setUnlimited(true);

        List<BonusMalusDTO> bonusMalusDTOList = List.of(bonusMalusDTO);

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> bonusMalusService.addBonusMalusByTeam(teamId, sprintId, bonusMalusDTOList));

        String expectedMessage = "Team for userId " + userId + " doesn't exist.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testValidateTeamBM_TeamNotFound() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setRoles(Collections.singleton(new Role(Role.RoleName.OS)));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        int sprintId = 1;
        int teamId = 1;

        when(teamService.getTeamByUserId(user.getId())).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> bonusMalusService.validateTeamBM(teamId, sprintId));

        String expectedMessage = "Team doesn't exist.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateTeamBM_shouldValidateBonusMalus_whenIsSSIsTrue() {
        // Arrange
        int teamId = 1;
        int sprintId = 1;

        User user = new User();
        user.setId(1);
        user.setRoles(Collections.singleton(new Role(Role.RoleName.SS)));

        Team team = new Team();
        team.setId(teamId);
        team.setUsers(Collections.singleton(user));

        Sprint sprint = new Sprint();
        sprint.setId(sprintId);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        BonusMalus bonusMalus = new BonusMalus();
        bonusMalus.setUnlimited(true);
        bonusMalus.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus.setAttributedTo(user);
        bonusMalus.setAttributedBy(user);
        bonusMalus.setTeam(team);
        bonusMalus.setSprint(sprint);
        List<BonusMalus> bonusMalusList = Collections.singletonList(bonusMalus);

        when(teamService.getTeamMembers(team.getId())).thenReturn(Arrays.asList(user));
        when(teamService.getTeamById(teamId)).thenReturn(team);
        when(sprintService.getSprintById(sprintId)).thenReturn(sprint);
        when(bonusMalusDao.findAllByTeamIdAndSprintId(teamId, sprintId)).thenReturn(bonusMalusList);
        when(bonusMalusDao.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);

        // Act
        bonusMalusService.validateTeamBM(teamId, sprintId);

        // Assert
        assertEquals(BonusMalus.BonusMalusStatus.PENDING, bonusMalus.getStatus());
    }

    @Test
    void validateTeamBM_shouldValidateBonusMalus_whenIsSSIsFalse() {
        // Arrange
        int teamId = 1;
        int sprintId = 1;

        User user = new User();
        user.setId(1);
        user.setRoles(Collections.singleton(new Role(Role.RoleName.OS)));

        Team team = new Team();
        team.setId(teamId);
        team.setUsers(Collections.singleton(user));

        Sprint sprint = new Sprint();
        sprint.setId(sprintId);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        BMValidation bmValidation = new BMValidation();
        bmValidation.setUser(user);
        bmValidation.setTeam(team);
        bmValidation.setSprint(sprint);

        BonusMalus bonusMalus = new BonusMalus();
        bonusMalus.setUnlimited(true);
        bonusMalus.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus.setAttributedTo(user);
        bonusMalus.setAttributedBy(user);
        bonusMalus.setTeam(team);
        bonusMalus.setSprint(sprint);
        List<BonusMalus> bonusMalusList = Collections.singletonList(bonusMalus);

        when(teamService.getTeamMembers(team.getId())).thenReturn(Arrays.asList(user));
        when(teamService.getTeamById(teamId)).thenReturn(team);
        when(teamService.getTeamByUserId(user.getId())).thenReturn(team);
        when(sprintService.getSprintById(sprintId)).thenReturn(sprint);
        when(bonusMalusDao.findAllByTeamIdAndSprintId(teamId, sprintId)).thenReturn(bonusMalusList);
        when(bonusMalusDao.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);
        when(bmValidationDao.save(any(BMValidation.class))).thenReturn(bmValidation);

        // Act
        bonusMalusService.validateTeamBM(teamId, sprintId);

        // Assert
        assertEquals(BonusMalus.BonusMalusStatus.PENDING, bonusMalus.getStatus());
    }

    @Test
    void validateTeamBM_shouldValidateBonusMalus_whenUserIsSSAndAllUsersValidateBmIsTrue() {
        // Arrange
        int teamId = 1;
        int sprintId = 1;

        User user = new User();
        user.setId(1);
        user.setRoles(Collections.singleton(new Role(Role.RoleName.SS)));

        Team team = new Team();
        team.setId(teamId);

        Sprint sprint = new Sprint();
        sprint.setId(sprintId);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        BMValidation bmValidation = new BMValidation();
        bmValidation.setUser(user);
        bmValidation.setTeam(team);
        bmValidation.setSprint(sprint);

        BonusMalus bonusMalus = new BonusMalus();
        bonusMalus.setUnlimited(true);
        bonusMalus.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus.setAttributedTo(user);
        bonusMalus.setAttributedBy(user);
        bonusMalus.setTeam(team);
        bonusMalus.setSprint(sprint);
        List<BonusMalus> bonusMalusList = Collections.singletonList(bonusMalus);

        when(teamService.getTeamMembers(team.getId())).thenReturn(Arrays.asList(user));
        when(teamService.getTeamById(teamId)).thenReturn(team);
        when(sprintService.getSprintById(sprintId)).thenReturn(sprint);
        when(bonusMalusDao.findAllByTeamIdAndSprintId(teamId, sprintId)).thenReturn(bonusMalusList);
        when(bonusMalusDao.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);
        when(bmValidationDao.save(any(BMValidation.class))).thenReturn(bmValidation);
        when(bmValidationDao.findByUserIdAndTeamIdAndSprintId(user.getId(), teamId, sprintId)).thenReturn(bmValidation);

        // Act
        bonusMalusService.validateTeamBM(teamId, sprintId);

        // Assert
        assertEquals(BonusMalus.BonusMalusStatus.VALIDATED, bonusMalus.getStatus());
        verify(bonusMalusDao, times(1)).findAllByTeamIdAndSprintId(teamId, sprintId);
        verify(bonusMalusDao, times(1)).saveAll(bonusMalusList);
    }

    @Test
    void testGetMembersWhoValidateBM() {
        // Arrange
        User user1 = new User();
        user1.setId(1);

        User user2 = new User();
        user2.setId(2);

        User user3 = new User();
        user3.setId(3);

        Team team = new Team();
        team.setId(1);
        team.setUsers(new HashSet<>(Arrays.asList(user1, user2, user3)));

        Sprint sprint = new Sprint();
        sprint.setId(1);

        BMValidation bmValidation1 = new BMValidation();
        bmValidation1.setUser(user1);
        bmValidation1.setTeam(team);
        bmValidation1.setSprint(sprint);

        BMValidation bmValidation3 = new BMValidation();
        bmValidation3.setUser(user3);
        bmValidation3.setTeam(team);
        bmValidation3.setSprint(sprint);

        when(teamService.getTeamMembers(team.getId())).thenReturn(Arrays.asList(user1, user2, user3));
        when(bmValidationDao.findByUserIdAndTeamIdAndSprintId(user1.getId(), team.getId(), sprint.getId())).thenReturn(bmValidation1);
        when(bmValidationDao.findByUserIdAndTeamIdAndSprintId(user2.getId(), team.getId(), sprint.getId())).thenReturn(null);
        when(bmValidationDao.findByUserIdAndTeamIdAndSprintId(user3.getId(), team.getId(), sprint.getId())).thenReturn(bmValidation3);

        // Act
        List<Integer> result = bonusMalusService.getMembersWhoValidateBM(team.getId(), sprint.getId());

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(user1.getId()));
        assertFalse(result.contains(user2.getId()));
        assertTrue(result.contains(user3.getId()));
    }

    @Test
    void testIsAllUsersValidateBm() throws Exception {
        // Arrange
        int teamId = 1;
        int sprintId = 1;

        User user1 = new User();
        user1.setId(1);

        User user2 = new User();
        user2.setId(2);

        Team team = new Team();
        team.setId(1);
        team.setUsers(new HashSet<>(Arrays.asList(user1, user2)));

        Sprint sprint = new Sprint();
        sprint.setId(1);

        BMValidation bmValidation1 = new BMValidation();
        bmValidation1.setUser(user1);
        bmValidation1.setTeam(team);
        bmValidation1.setSprint(sprint);

        when(teamService.getTeamMembers(teamId)).thenReturn(Arrays.asList(user1, user2));
        when(bmValidationDao.findByUserIdAndTeamIdAndSprintId(user1.getId(), teamId, sprintId)).thenReturn(bmValidation1);
        when(bmValidationDao.findByUserIdAndTeamIdAndSprintId(user2.getId(), teamId, sprintId)).thenReturn(null);

        // Act
        Method method = BonusMalusService.class.getDeclaredMethod("isAllUsersValidateBm", int.class, int.class);
        method.setAccessible(true);
        method.invoke(bonusMalusService, teamId, sprintId);

        // Assert
        verify(bmValidationDao, times(2)).findByUserIdAndTeamIdAndSprintId(anyInt(), anyInt(), anyInt());
    }

    @Test
    void testGetBonusMalusStudentByTeamIdAndSprintIdValidated() {
        // Arrange
        int userId = 1;
        int sprintId = 1;

        BonusMalus bonusMalus = new BonusMalus();
        bonusMalus.setAttributedTo(new User());
        bonusMalus.getAttributedTo().setId(userId);
        bonusMalus.setSprint(new Sprint());
        bonusMalus.getSprint().setId(sprintId);
        bonusMalus.setUnlimited(false);
        bonusMalus.setStatus(BonusMalus.BonusMalusStatus.VALIDATED);

        when(bonusMalusDao.findByAttributedToIdAndSprintIdAndIsUnlimitedAndStatus(userId, sprintId, false, BonusMalus.BonusMalusStatus.VALIDATED)).thenReturn(bonusMalus);

        // Act
        BonusMalus result = bonusMalusService.getBonusMalusStudentByTeamIdAndSprintIdValidated(userId, sprintId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getAttributedTo().getId());
        assertEquals(sprintId, result.getSprint().getId());
        assertFalse(result.isUnlimited());
        assertEquals(BonusMalus.BonusMalusStatus.VALIDATED, result.getStatus());
    }

    @Test
    void testGetBonusMalusSSByTeamIdAndSprintIdValidated() {
        // Arrange
        int userId = 1;
        int sprintId = 1;

        BonusMalus bonusMalus = new BonusMalus();
        bonusMalus.setAttributedTo(new User());
        bonusMalus.getAttributedTo().setId(userId);
        bonusMalus.setSprint(new Sprint());
        bonusMalus.getSprint().setId(sprintId);
        bonusMalus.setUnlimited(true);
        bonusMalus.setStatus(BonusMalus.BonusMalusStatus.VALIDATED);

        when(bonusMalusDao.findByAttributedToIdAndSprintIdAndIsUnlimitedAndStatus(userId, sprintId, true, BonusMalus.BonusMalusStatus.VALIDATED)).thenReturn(bonusMalus);

        // Act
        BonusMalus result = bonusMalusService.getBonusMalusSSByTeamIdAndSprintIdValidated(userId, sprintId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getAttributedTo().getId());
        assertEquals(sprintId, result.getSprint().getId());
        assertTrue(result.isUnlimited());
        assertEquals(BonusMalus.BonusMalusStatus.VALIDATED, result.getStatus());
    }

    @Test
    void testGetBonusMalusValueForStudent() {
        int userId = 1;
        int sprintId = 1;
        float expectedValue = 10.0f;

        BonusMalus bonusMalus = new BonusMalus();
        bonusMalus.setValue(expectedValue);
        bonusMalus.setStatus(BonusMalus.BonusMalusStatus.VALIDATED);
        bonusMalus.setUnlimited(false);

        when(bonusMalusDao.findByAttributedToIdAndSprintIdAndIsUnlimitedAndStatus(userId, sprintId, false, BonusMalus.BonusMalusStatus.VALIDATED)).thenReturn(bonusMalus);

        double result = bonusMalusService.getBonusMalusValue(userId, sprintId, GradeTypes.GradeTypesEnum.TEBM.getId());

        assertEquals(expectedValue, result);
    }

    @Test
    void testGetBonusMalusValueForSS() {
        int userId = 1;
        int sprintId = 1;
        float expectedValue = 20.0f;

        BonusMalus bonusMalus = new BonusMalus();
        bonusMalus.setValue(expectedValue);
        bonusMalus.setStatus(BonusMalus.BonusMalusStatus.VALIDATED);
        bonusMalus.setUnlimited(true);

        when(bonusMalusDao.findByAttributedToIdAndSprintIdAndIsUnlimitedAndStatus(userId, sprintId, true, BonusMalus.BonusMalusStatus.VALIDATED)).thenReturn(bonusMalus);

        double result = bonusMalusService.getBonusMalusValue(userId, sprintId, GradeTypes.GradeTypesEnum.SSBM.getId());

        assertEquals(expectedValue, result);
    }

    @Test
    void testGetBonusMalusValueWhenNoBonusMalusFound() {
        int userId = 1;
        int sprintId = 1;

        when(bonusMalusDao.findByAttributedToIdAndSprintIdAndIsUnlimitedAndStatus(userId, sprintId, false, BonusMalus.BonusMalusStatus.VALIDATED)).thenReturn(null);
        when(bonusMalusDao.findByAttributedToIdAndSprintIdAndIsUnlimitedAndStatus(userId, sprintId, true, BonusMalus.BonusMalusStatus.VALIDATED)).thenReturn(null);

        double resultForStudent = bonusMalusService.getBonusMalusValue(userId, sprintId, GradeTypes.GradeTypesEnum.TEBM.getId());
        double resultForSS = bonusMalusService.getBonusMalusValue(userId, sprintId, GradeTypes.GradeTypesEnum.SSBM.getId());

        assertEquals(0, resultForStudent);
        assertEquals(0, resultForSS);
    }

    @Test
    void getUBmByTeamAndSprint_shouldReturnBonusMalusDTOList() {
        // Arrange
        int teamId = 1;
        int sprintId = 1;

        User user = new User();
        user.setId(1);

        Team team = new Team();
        team.setId(teamId);

        Sprint sprint = new Sprint();
        sprint.setId(sprintId);

        BonusMalus bonusMalus = new BonusMalus();
        bonusMalus.setUnlimited(true);
        bonusMalus.setStatus(BonusMalus.BonusMalusStatus.PENDING);
        bonusMalus.setAttributedTo(user);
        bonusMalus.setAttributedBy(user);
        bonusMalus.setTeam(team);
        bonusMalus.setSprint(sprint);
        List<BonusMalus> bonusMalusList = Collections.singletonList(bonusMalus);

        when(bonusMalusDao.findAllByTeamIdAndSprintId(teamId, sprintId)).thenReturn(bonusMalusList);

        // Act
        List<BonusMalusDTO> actualBonusMalusDTOList = bonusMalusService.getUBmByTeamAndSprint(teamId, sprintId);

        // Assert
        assertEquals(1, actualBonusMalusDTOList.size());
        assertTrue(actualBonusMalusDTOList.get(0).isUnlimited());
        verify(bonusMalusDao, times(1)).findAllByTeamIdAndSprintId(teamId, sprintId);
    }
}


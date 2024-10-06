package com.example.backend.service;

import com.example.backend.dao.FlagDao;
import com.example.backend.dto.FlagDto;
import com.example.backend.model.Flag;
import com.example.backend.model.Team;
import com.example.backend.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlagServiceTest {

    @Mock
    private FlagDao flagDao;

    @Mock
    private UserService userService;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private FlagService flagService;

    @Test
    void getAllFlags_shouldReturnList() {
        // Setup
        when(flagDao.findAll()).thenReturn(Collections.emptyList());

        // Execution
        flagService.getAllFlags();

        // Verification
        verify(flagDao, times(1)).findAll();
    }

    @Test
    void getFlagById_shouldReturnFlag() {
        // Setup
        int id = 1;
        Flag flag = new Flag();
        when(flagDao.findById(id)).thenReturn(Optional.of(flag));

        // Execution
        Flag result = flagService.getFlagById(id);

        // Verification
        assertEquals(flag, result);
        verify(flagDao, times(1)).findById(id);
    }

    @Test
    void createFlag_shouldReturnFlag() {
        // Setup
        FlagDto flagDto = new FlagDto();
        Flag flag = new Flag();
        when(flagDao.save(any(Flag.class))).thenReturn(flag);

        // Define behavior for userService and teamService
        when(userService.getUserById(anyInt())).thenReturn(new User());
        when(teamService.getTeamById(anyInt())).thenReturn(new Team());

        // Execution
        Flag result = flagService.createFlag(flagDto);

        // Verification
        assertEquals(flag, result);
        verify(flagDao, times(1)).save(any(Flag.class));
    }

    @Test
    void deleteFlagById_shouldCallDeleteMethod() {
        int id = 1;

        // Execution
        flagService.deleteFlagById(id);

        // Verification
        verify(flagDao, times(1)).deleteById(id);
    }

    @Test
    void saveFlag_shouldReturnFlag() {
        // Setup
        Flag flag = new Flag();
        when(flagDao.save(flag)).thenReturn(flag);

        // Execution
        Flag result = flagService.saveFlag(flag);

        // Verification
        assertEquals(flag, result);
        verify(flagDao, times(1)).save(flag);
    }
}
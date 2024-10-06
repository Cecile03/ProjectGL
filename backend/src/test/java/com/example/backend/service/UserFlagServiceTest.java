package com.example.backend.service;

import com.example.backend.dao.UserFlagDao;
import com.example.backend.dto.FlagDto;
import com.example.backend.dto.UserFlagDto;
import com.example.backend.dto.UserInteract;
import com.example.backend.model.Flag;
import com.example.backend.model.User;
import com.example.backend.model.UserFlag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFlagServiceTest {

    @Mock
    private UserFlagDao userFlagDao;

    @InjectMocks
    private UserFlagService userFlagService;

    @Mock
    private FlagService flagService;

    @Mock
    private UserService userService;

    @Test
    void getAllUserFlags_shouldReturnList() {
        // Setup
        when(userFlagDao.findAll()).thenReturn(Collections.emptyList());

        // Execution
        userFlagService.getAllUserFlags();

        // Verification
        verify(userFlagDao, times(1)).findAll();
    }

    @Test
    void saveUserFlag_shouldReturnUserFlag() {
        // Setup
        UserFlag userFlag = new UserFlag();
        when(userFlagDao.save(userFlag)).thenReturn(userFlag);

        // Execution
        userFlagService.saveUserFlag(userFlag);

        // Verification
        verify(userFlagDao, times(1)).save(userFlag);
    }

    @Test
    void setValidated_shouldReturnUserFlag() {
        // Setup
        int id = 1;
        boolean validated = true;
        UserFlag userFlag = new UserFlag();
        userFlag.setId(id);
        userFlag.setValidated(validated);
        when(userFlagDao.findById(id)).thenReturn(Optional.of(userFlag));
        when(userFlagDao.save(userFlag)).thenReturn(userFlag);

        // Execution
        UserFlag result = userFlagService.setValidated(id, validated);

        // Verification
        assertEquals(validated, result.getValidated());
        verify(userFlagDao, times(1)).save(userFlag);
    }

    @Test
    void getAllUserFlagByFlagId_shouldThrowException() {
        // Setup
        int flagId = 1;
        when(userFlagDao.findByFlag_Id(flagId)).thenThrow(new RuntimeException("Database error"));

        // Execution & Verification
        assertThrows(RuntimeException.class, () -> userFlagService.getAllUserFlagByFlagId(flagId));
        verify(userFlagDao, times(1)).findByFlag_Id(flagId);
    }

    @Test
    void deleteUserFlagsByFlagIdShouldCallDeleteAllInUserFlagDao() {
        int flagId = 1;
        userFlagService.deleteUserFlagsByFlagId(flagId);
        verify(userFlagDao, times(1)).deleteAll(anyList());
    }

    @Test
    void areAllUserFlagsValidatedByFlagIdShouldReturnTrueWhenAllUserFlagsAreValidated() {
        int flagId = 1;
        UserFlag userFlag1 = mock(UserFlag.class);
        when(userFlag1.getValidated()).thenReturn(true);
        UserFlag userFlag2 = mock(UserFlag.class);
        when(userFlag2.getValidated()).thenReturn(true);
        List<UserFlag> userFlags = Arrays.asList(userFlag1, userFlag2);
        when(userFlagDao.findByFlag_Id(flagId)).thenReturn(userFlags);

        boolean result = userFlagService.areAllUserFlagsValidatedByFlagId(flagId);

        assertTrue(result);
    }

    @Test
    void areAllUserFlagsValidatedByFlagIdShouldReturnFalseWhenAtLeastOneUserFlagIsNotValidated() {
        int flagId = 1;
        UserFlag userFlag1 = mock(UserFlag.class);
        when(userFlag1.getValidated()).thenReturn(true);
        UserFlag userFlag2 = mock(UserFlag.class);
        when(userFlag2.getValidated()).thenReturn(false);
        List<UserFlag> userFlags = Arrays.asList(userFlag1, userFlag2);
        when(userFlagDao.findByFlag_Id(flagId)).thenReturn(userFlags);

        boolean result = userFlagService.areAllUserFlagsValidatedByFlagId(flagId);

        assertFalse(result);
    }

}
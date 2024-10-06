package com.example.backend.controller;

import com.example.backend.dto.FlagDto;
import com.example.backend.model.Flag;
import com.example.backend.service.FlagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlagControllerTest {

    @Mock
    private FlagService flagService;

    @InjectMocks
    private FlagController flagController;

    @Test
    void getAllFlags_shouldReturnList() {
        // Setup
        when(flagService.getAllFlags()).thenReturn(Collections.emptyList());

        // Execution
        flagController.getAllFlags();

        // Verification
        verify(flagService, times(1)).getAllFlags();
    }

    @Test
    void getFlagById_shouldReturnFlag() {
        // Setup
        int id = 1;
        when(flagService.getFlagById(id)).thenReturn(new Flag());

        // Execution
        flagController.getFlagById(id);

        // Verification
        verify(flagService, times(1)).getFlagById(id);
    }

    @Test
    void createFlag_shouldReturnFlag() {
        // Setup
        FlagDto flagDto = new FlagDto();
        when(flagService.createFlag(flagDto)).thenReturn(new Flag());

        // Execution
        flagController.createFlag(flagDto);

        // Verification
        verify(flagService, times(1)).createFlag(flagDto);
    }

    @Test
    void deleteFlagById_shouldCallDeleteMethod() {
        // Setup
        int id = 1;

        // Execution
        flagController.deleteFlagById(id);

        // Verification
        verify(flagService, times(1)).deleteFlagById(id);
    }

}
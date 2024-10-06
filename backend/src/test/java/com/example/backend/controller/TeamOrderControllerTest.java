package com.example.backend.controller;

import com.example.backend.model.TeamOrder;
import com.example.backend.service.TeamOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TeamOrderControllerTest {

    @InjectMocks
    private TeamOrderController teamOrderController;

    @Mock
    private TeamOrderService teamOrderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateTeamOrder_shouldReturnOkWhenSuccessful() {
        doNothing().when(teamOrderService).updateTeamOrder(anyInt(), anyInt(), anyList());

        ResponseEntity<Void> response = teamOrderController.updateTeamOrder(1, 1, Arrays.asList(1, 2, 3));

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void updateTeamOrder_shouldReturnBadRequestWhenExceptionThrown() {
        doThrow(new RuntimeException()).when(teamOrderService).updateTeamOrder(anyInt(), anyInt(), anyList());

        ResponseEntity<Void> response = teamOrderController.updateTeamOrder(1, 1, Arrays.asList(1, 2, 3));

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void getTeamOrder_shouldReturnOkWhenSuccessful() {
        TeamOrder expectedTeamOrder = new TeamOrder();
        when(teamOrderService.getTeamOrder(anyInt(), anyInt())).thenReturn(expectedTeamOrder);

        ResponseEntity<TeamOrder> response = teamOrderController.getTeamOrder(1, 1);

        assertEquals(ResponseEntity.ok(expectedTeamOrder), response);
    }

    @Test
    void getTeamOrder_shouldReturnBadRequestWhenExceptionThrown() {
        when(teamOrderService.getTeamOrder(anyInt(), anyInt())).thenThrow(new RuntimeException());

        ResponseEntity<TeamOrder> response = teamOrderController.getTeamOrder(1, 1);

        assertEquals(ResponseEntity.status(400).build(), response);
    }
}
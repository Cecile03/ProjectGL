package com.example.backend.service;

import com.example.backend.dao.TeamOrderDao;
import com.example.backend.model.TeamOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class TeamOrderServiceTest {

    @InjectMocks
    private TeamOrderService teamOrderService;

    @Mock
    private TeamOrderDao teamOrderDao;

    @Mock
    private TeamService teamService;

    @Mock
    private SprintService sprintService;

    @Mock
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTeamOrder() {
        when(teamOrderDao.findByTeamIdAndSprintId(anyInt(), anyInt())).thenReturn(Optional.of(new TeamOrder()));
        teamOrderService.getTeamOrder(1, 1);
        verify(teamOrderDao, times(1)).findByTeamIdAndSprintId(anyInt(), anyInt());
    }

    @Test
    void testSaveTeamOrder() {
        int teamId = 1;
        int sprintId = 2;
        List<Integer> userIds = Arrays.asList(1, 2, 3);

        teamOrderService.saveTeamOrder(teamId, sprintId, userIds);

        verify(teamOrderDao, times(1)).save(any(TeamOrder.class));
    }

    @Test
    void testUpdateTeamOrder() {
        int teamId = 1;
        int sprintId = 2;
        List<Integer> userIds = Arrays.asList(1, 2, 3);

        when(teamOrderDao.findByTeamIdAndSprintId(anyInt(), anyInt()))
                .thenReturn(Optional.of(new TeamOrder()));

        teamOrderService.updateTeamOrder(teamId, sprintId, userIds);

        verify(teamOrderDao, times(1)).save(any(TeamOrder.class));
    }

    @Test
    void testUpdateTeamOrderException() {
        int teamId = 1;
        int sprintId = 2;
        List<Integer> userIds = Arrays.asList(1, 2, 3);

        when(teamOrderDao.findByTeamIdAndSprintId(anyInt(), anyInt()))
                .thenThrow(new RuntimeException());

        teamOrderService.updateTeamOrder(teamId, sprintId, userIds);

        verify(teamOrderDao, times(1)).save(any(TeamOrder.class));
    }

}
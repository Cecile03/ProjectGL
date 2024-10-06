package com.example.backend.service;

import com.example.backend.model.Criteria;
import com.example.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamBuilderTest {

    private List<User> users;
    private Criteria criteria;

    @BeforeEach
    public void init() {
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
        user4.setBachelor(true);
        user4.setGradePast(80.0);

        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

        criteria = mock(Criteria.class);
    }

    @Test
    void testBuildTeams() {

        when(criteria.getNumberOfTeams()).thenReturn(2);
        when(criteria.getNumberOfGirls()).thenReturn(1);

        List<List<User>> teams = TeamBuilder.buildTeams(users, criteria);

        assertEquals(2, teams.size());
    }

    @Test
    void testDistributeEquitably() {
        List<Integer> distribution = TeamBuilder.distributeEquitably(5, 2);

        assertEquals(2, distribution.size());
        assertEquals(3, distribution.get(0));
        assertEquals(2, distribution.get(1));
    }

    @Test
    void testDistributeGirls() {
        List<Integer> distribution = Arrays.asList(3, 2);
        List<Integer> girlsDistribution = TeamBuilder.distributeGirls(2, distribution, 1);

        assertEquals(2, girlsDistribution.size());
        assertEquals(1, girlsDistribution.get(0));
        assertEquals(1, girlsDistribution.get(1));
    }

    @Test
    void testBuildTeamsWithNullGrade() {
        User userWithNullGrade = new User();
        userWithNullGrade.setGender("male");
        userWithNullGrade.setBachelor(true);
        userWithNullGrade.setGradePast(null);

        users.add(userWithNullGrade);

        when(criteria.getNumberOfTeams()).thenReturn(2);
        when(criteria.getNumberOfGirls()).thenReturn(1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> TeamBuilder.buildTeams(users, criteria));

        String expectedMessage = "User null has no grade";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDistributeGirlsConditionNotMet() {
        List<Integer> distribution = Arrays.asList(2, 2); // total teams = 4
        int totalGirls = 10; // total girls = 10
        int girlsPerTeam = 2; // girls per team = 2

        // totalGirls / girlsPerTeam = 5 which is greater than distribution.size() = 4

        List<Integer> girlsDistribution = TeamBuilder.distributeGirls(totalGirls, distribution, girlsPerTeam);

        // Assert the size of the distribution
        assertEquals(2, girlsDistribution.size());
    }

    @Test
    void testBuildTeamsDistributeEvenlyConditionNotMet() {
        // Create a list of users
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setGender("female");
            user.setBachelor(true);
            user.setGradePast(85.0);
            users.add(user);
        }

        // Create criteria
        Criteria criteria = new Criteria();
        criteria.setNumberOfTeams(3);
        criteria.setNumberOfGirls(2);

        // Call the buildTeams method
        List<List<User>> teams = TeamBuilder.buildTeams(users, criteria);

        // Assert the size of the teams
        assertEquals(3, teams.size());

        // Assert the distribution of users
        // The exact distribution will depend on the implementation of distributeEvenly method when the condition is not met
        // Assuming that the remaining users are distributed evenly among the teams
        assertEquals(2, teams.get(0).size());
        assertEquals(1, teams.get(1).size());
        assertEquals(0, teams.get(2).size());
    }

    @Test
    void testBalanceTeams() {

        // Create criteria
        Criteria criteria = new Criteria();
        criteria.setNumberOfTeams(2);
        criteria.setNumberOfGirls(2);

        List<List<User>> teams = TeamBuilder.buildTeams(users, criteria);

        List<List<User>> balancedTeams = TeamBuilder.balanceTeams(teams, criteria);

        // Calculate the average grades of the balanced teams
        double[] averages = new double[balancedTeams.size()];
        for (int i = 0; i < balancedTeams.size(); i++) {
            averages[i] = TeamBuilder.calculerMoyenne(balancedTeams.get(i));
        }

        // Calculate the difference between the highest and lowest average grades
        double diff = TeamBuilder.ecartMoyennes(averages);

        // Assert that the difference is less than or equal to the threshold
        assertTrue(diff <= criteria.getMinAverageThreshold());

        assertEquals(2, teams.size());

    }
}
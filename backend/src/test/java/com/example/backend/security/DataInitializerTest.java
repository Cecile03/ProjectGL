package com.example.backend.security;

import com.example.backend.dao.GradeTypesDao;
import com.example.backend.dao.RoleDao;
import com.example.backend.model.GradeTypes;
import com.example.backend.model.Role;
import com.example.backend.model.Role.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private RoleDao roleRepository;

    @Mock
    private GradeTypesDao gradeTypesDao;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Captor
    private ArgumentCaptor<List<Role>> roleCaptor;

    @BeforeEach
    void setUp() {

    }

    @Test
    void initRoles_shouldSaveDefaultRolesWhenRepositoryIsEmpty() {
        // Arrange
        when(roleRepository.count()).thenReturn(0L);

        List<RoleName> expectedRoleNames = List.of(
                RoleName.SS,
                RoleName.PL,
                RoleName.OL,
                RoleName.OS,
                RoleName.TC
        );

        // Act
        dataInitializer.initRoles();

        // Assert
        verify(roleRepository, times(1)).count();
        verify(roleRepository, times(1)).saveAll(roleCaptor.capture());

        List<Role> capturedRoles = roleCaptor.getValue();
        assertEquals(expectedRoleNames.size(), capturedRoles.size());

        for (Role role : capturedRoles) {
            assertTrue(expectedRoleNames.contains(role.getName()));
        }
    }

    @Test
    void initRoles_shouldNotSaveDefaultRolesWhenRepositoryIsNotEmpty() {
        // Arrange
        when(roleRepository.count()).thenReturn(5L);

        // Act
        dataInitializer.initRoles();

        // Assert
        verify(roleRepository, times(1)).count();
        verify(roleRepository, never()).saveAll(any());
    }

    @Test
    void initGradeTypesShouldSaveDefaultGradeTypesWhenRepositoryIsEmpty() {
        when(gradeTypesDao.count()).thenReturn(0L);

        dataInitializer.initGradeTypes();

        verify(gradeTypesDao, times(1)).count();
    }

    @Test
    void initGradeTypesShouldNotSaveDefaultGradeTypesWhenRepositoryIsNotEmpty() {
        when(gradeTypesDao.count()).thenReturn(5L);

        dataInitializer.initGradeTypes();

        verify(gradeTypesDao, times(1)).count();
        verify(gradeTypesDao, never()).saveAll(any());
    }

    @Test
    void initDataShouldCallInitRolesAndInitGradeTypes() {
        dataInitializer.initData();

        verify(roleRepository, times(1)).count();
        verify(gradeTypesDao, times(1)).count();
    }
}

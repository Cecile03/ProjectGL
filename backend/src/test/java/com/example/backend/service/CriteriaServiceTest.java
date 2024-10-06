package com.example.backend.service;

import com.example.backend.dao.CriteriaDao;
import com.example.backend.model.Criteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CriteriaServiceTest {

    @InjectMocks
    private CriteriaService criteriaService;

    @Mock
    private CriteriaDao criteriaDao;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCriteria_whenCriteriaIsNull() {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> criteriaService.save(null));

        String expectedMessage = "Criteria is null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testSaveCriteria_whenCriteriaAlreadyExists() {
        Criteria criteria = new Criteria();
        criteria.setId(1);

        when(criteriaDao.existsById(criteria.getId())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> criteriaService.save(criteria));

        String expectedMessage = "Criteria already exists";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCreateCriteria() {
        Criteria criteria = new Criteria();
        when(criteriaDao.save(criteria)).thenReturn(criteria);

        assertEquals(criteria, criteriaService.save(criteria));
        verify(criteriaDao, times(1)).save(criteria);
    }

    @Test
    void testDeleteCriteria() {
        Criteria criteria = new Criteria();
        doNothing().when(criteriaDao).delete(criteria);

        criteriaService.delete(criteria);
        verify(criteriaDao, times(1)).delete(criteria);
    }

    @Test
    void testUpdateCriteria() {
        Criteria criteria = new Criteria();
        when(criteriaDao.save(criteria)).thenReturn(criteria);

        assertEquals(criteria, criteriaService.update(criteria));
        verify(criteriaDao, times(1)).save(criteria);
    }

    @Test
    void testUpdateCriteria_whenCriteriaExists() {
        Criteria criteria = new Criteria();
        criteria.setId(1);

        when(criteriaDao.existsById(criteria.getId())).thenReturn(true);
        when(criteriaDao.save(criteria)).thenReturn(criteria);

        Criteria updatedCriteria = criteriaService.update(criteria);

        assertEquals(criteria, updatedCriteria);
        verify(criteriaDao, times(1)).save(criteria);
    }

    @Test
    void testUpdateCriteria_whenCriteriaIsNull() {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> criteriaService.update(null));

        String expectedMessage = "Criteria is null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testGetCriteriaById() {
        int id = 1;
        Criteria criteria = new Criteria();
        when(criteriaDao.findById(id)).thenReturn(Optional.of(criteria));

        assertEquals(criteria, criteriaService.findById(id));
        verify(criteriaDao, times(1)).findById(id);
    }

    @Test
    void testDeleteAll() {
        criteriaService.deleteAll();
        verify(criteriaDao, times(1)).deleteAll();
    }
}
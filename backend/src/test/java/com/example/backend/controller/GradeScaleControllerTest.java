package com.example.backend.controller;

import com.example.backend.dto.GradeScaleDTO;
import com.example.backend.model.GradeScale;
import com.example.backend.service.GradeScaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GradeScaleControllerTest {

    @InjectMocks
    private GradeScaleController gradeScaleController;

    @Mock
    private GradeScaleService gradeScaleService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFullGradeScales_shouldReturnOkWhenSuccessful() {
        List<GradeScaleDTO> gradeScalesDTO = Collections.singletonList(new GradeScaleDTO());
        doNothing().when(gradeScaleService).createFullGradeScales(gradeScalesDTO);

        ResponseEntity<Void> response = gradeScaleController.createFullGradeScales(gradeScalesDTO);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void createFullGradeScales_shouldReturnBadRequestWhenExceptionThrown() {
        List<GradeScaleDTO> gradeScalesDTO = Collections.singletonList(new GradeScaleDTO());
        doThrow(new RuntimeException()).when(gradeScaleService).createFullGradeScales(gradeScalesDTO);

        ResponseEntity<Void> response = gradeScaleController.createFullGradeScales(gradeScalesDTO);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void update_shouldReturnOkWhenSuccessful() {
        int id = 1;
        GradeScale newGradeScaleData = new GradeScale();
        doNothing().when(gradeScaleService).update(id, newGradeScaleData);

        ResponseEntity<Void> response = gradeScaleController.update(id, newGradeScaleData);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void update_shouldReturnInternalServerErrorWhenExceptionThrown() {
        int id = 1;
        GradeScale newGradeScaleData = new GradeScale();
        doThrow(new RuntimeException()).when(gradeScaleService).update(id, newGradeScaleData);

        ResponseEntity<Void> response = gradeScaleController.update(id, newGradeScaleData);

        assertEquals(ResponseEntity.status(500).build(), response);
    }

    @Test
    void delete_shouldReturnOkWhenSuccessful() {
        int id = 1;
        doNothing().when(gradeScaleService).delete(id);

        ResponseEntity<Void> response = gradeScaleController.delete(id);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void delete_shouldReturnInternalServerErrorWhenExceptionThrown() {
        int id = 1;
        doThrow(new RuntimeException()).when(gradeScaleService).delete(id);

        ResponseEntity<Void> response = gradeScaleController.delete(id);

        assertEquals(ResponseEntity.status(500).build(), response);
    }

    @Test
    void getGradeScaleById_shouldReturnOkWhenSuccessful() {
        int id = 1;
        GradeScale expectedGradeScale = new GradeScale();
        when(gradeScaleService.getGradeScaleById(id)).thenReturn(expectedGradeScale);

        ResponseEntity<GradeScale> response = gradeScaleController.getGradeScaleById(id);

        assertEquals(ResponseEntity.ok(expectedGradeScale), response);
    }

    @Test
    void getGradeScaleById_shouldReturnInternalServerErrorWhenExceptionThrown() {
        int id = 1;
        when(gradeScaleService.getGradeScaleById(id)).thenThrow(new RuntimeException());

        ResponseEntity<GradeScale> response = gradeScaleController.getGradeScaleById(id);

        assertEquals(ResponseEntity.status(500).build(), response);
    }

    @Test
    void getGradeScale_shouldReturnOkWhenSuccessful() {
        List<GradeScale> expectedGradeScales = Collections.singletonList(new GradeScale());
        when(gradeScaleService.getAll()).thenReturn(expectedGradeScales);

        ResponseEntity<List<GradeScale>> response = gradeScaleController.getGradeScale();

        assertEquals(ResponseEntity.ok(expectedGradeScales), response);
    }

    @Test
    void deleteAll_shouldReturnOkWhenSuccessful() {
        doNothing().when(gradeScaleService).deleteAll();

        ResponseEntity<Void> response = gradeScaleController.deleteAll();

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void deleteAll_shouldReturnInternalServerErrorWhenExceptionThrown() {
        doThrow(new RuntimeException()).when(gradeScaleService).deleteAll();

        ResponseEntity<Void> response = gradeScaleController.deleteAll();

        assertEquals(ResponseEntity.status(500).build(), response);
    }
}
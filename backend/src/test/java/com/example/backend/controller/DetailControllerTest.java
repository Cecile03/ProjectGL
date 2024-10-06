package com.example.backend.controller;

import com.example.backend.model.Detail;
import com.example.backend.service.DetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DetailControllerTest {

    @InjectMocks
    DetailController detailController;

    @Mock
    DetailService detailService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addDetail() {
        Detail detail = new Detail();

        ResponseEntity<Detail> response = detailController.add(detail);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(detail, response.getBody());
    }

    @Test
    void add_shouldReturnInternalServerErrorWhenExceptionThrown() {
        // Setup
        Detail detail = new Detail();
        doThrow(new RuntimeException()).when(detailService).createDetail(detail);

        // Execution
        ResponseEntity<Detail> response = detailController.add(detail);

        // Verification
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(detailService, times(1)).createDetail(detail);
    }

    @Test
    void updateDetail() {
        Detail newDetailData = new Detail();
        int id = 1;

        ResponseEntity<Detail> response = detailController.update(id, newDetailData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(detailService, times(1)).update(id, newDetailData);
    }

    @Test
    void update_shouldReturnInternalServerErrorWhenExceptionThrown() {
        // Setup
        Detail newDetailData = new Detail();
        int id = 1;
        when(detailService.update(id, newDetailData)).thenThrow(new RuntimeException());

        // Execution
        ResponseEntity<Detail> response = detailController.update(id, newDetailData);

        // Verification
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(detailService, times(1)).update(id, newDetailData);
    }

    @Test
    void deleteDetail() {
        int id = 1;

        ResponseEntity<Void> response = detailController.delete(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(detailService, times(1)).delete(id);
    }

    @Test
    void delete_shouldReturnInternalServerErrorWhenExceptionThrown() {
        // Setup
        int id = 1;
        doThrow(new RuntimeException()).when(detailService).delete(id);

        // Execution
        ResponseEntity<Void> response = detailController.delete(id);

        // Verification
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(detailService, times(1)).delete(id);
    }

    @Test
    void getDetailById() {
        int id = 1;
        Detail detail = new Detail();
        when(detailService.getDetailById(id)).thenReturn(detail);

        ResponseEntity<Detail> response = detailController.getDetailById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(detail, response.getBody());
    }

    @Test
    void getDetailById_shouldReturnInternalServerErrorWhenExceptionThrown() {
        // Setup
        int id = 1;
        when(detailService.getDetailById(id)).thenThrow(new RuntimeException());

        // Execution
        ResponseEntity<Detail> response = detailController.getDetailById(id);

        // Verification
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(detailService, times(1)).getDetailById(id);
    }

    @Test
    void getAllDetails() {
        List<Detail> details = Arrays.asList(new Detail(), new Detail());
        when(detailService.getAll()).thenReturn(details);

        ResponseEntity<List<Detail>> response = detailController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(details, response.getBody());
    }

    @Test
    void getDetailsByCategoryId_shouldReturnDetailsWhenSuccessful() {
        // Setup
        int categoryId = 1;
        List<Detail> expectedDetails = Arrays.asList(new Detail(), new Detail());
        when(detailService.getDetailsByCategoryId(categoryId)).thenReturn(expectedDetails);

        // Execution
        ResponseEntity<List<Detail>> response = detailController.getDetailsByCategoryId(categoryId);

        // Verification
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDetails, response.getBody());
        verify(detailService, times(1)).getDetailsByCategoryId(categoryId);
    }

    @Test
    void deleteAll_shouldReturnOkWhenSuccessful() {
        // Execution
        ResponseEntity<Void> response = detailController.deleteAll();

        // Verification
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(detailService, times(1)).deleteAll();
    }

    @Test
    void deleteAll_shouldReturnInternalServerErrorWhenExceptionThrown() {
        // Setup
        doThrow(new RuntimeException()).when(detailService).deleteAll();

        // Execution
        ResponseEntity<Void> response = detailController.deleteAll();

        // Verification
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(detailService, times(1)).deleteAll();
    }
}
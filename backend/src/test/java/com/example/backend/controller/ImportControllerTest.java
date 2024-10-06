package com.example.backend.controller;

import com.example.backend.dto.StudentDTO;
import com.example.backend.service.CsvProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ImportControllerTest {

    @InjectMocks
    private ImportController importController;

    @Mock
    private CsvProcessingService csvProcessingService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleFileUpload_shouldReturnStudentDTOsWhenSuccessful() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());
        List<StudentDTO> expectedStudents = Collections.singletonList(new StudentDTO());
        when(csvProcessingService.processCsvFile(file)).thenReturn(expectedStudents);

        List<StudentDTO> result = importController.handleFileUpload(file);

        assertEquals(expectedStudents, result);
    }

    @Test
    void handleFileUpload_shouldThrowResponseStatusExceptionWhenFileIsEmpty() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "".getBytes());

        assertThrows(ResponseStatusException.class, () -> importController.handleFileUpload(file));
    }

    @Test
    void handleFileUpload_shouldThrowResponseStatusExceptionWhenCsvProcessingFails() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());
        when(csvProcessingService.processCsvFile(file)).thenThrow(new RuntimeException());

        assertThrows(ResponseStatusException.class, () -> importController.handleFileUpload(file));
    }
}
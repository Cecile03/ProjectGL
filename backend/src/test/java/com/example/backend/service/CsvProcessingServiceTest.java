package com.example.backend.service;

import com.example.backend.dto.StudentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CsvProcessingServiceTest {

    @Mock
    private MultipartFile file;

    private CsvProcessingService csvProcessingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        csvProcessingService = new CsvProcessingService();
    }

    @Test
    void shouldProcessCsvFileSuccessfully() throws Exception {
        String content = "id;fullName;gender;bachelor;average;padl;pdlo;pwnd;irs;stages7;s5;s6\n" +
                "1;John Doe;Male;Computer Science;15.5;12.5;13.5;14.5;16.5;17.5;18.5;19.5";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", content.getBytes());

        List<StudentDTO> students = csvProcessingService.processCsvFile(file);

        assertEquals(1, students.size());
        assertEquals(1, students.get(0).getId());
        assertEquals("John Doe", students.get(0).getFullName());
        assertEquals("Male", students.get(0).getGender());
        assertEquals("Computer Science", students.get(0).getBachelor());
        assertEquals(15.5, students.get(0).getAverage());
        assertEquals(12.5, students.get(0).getPadl());
        assertEquals(13.5, students.get(0).getPdlo());
        assertEquals(14.5, students.get(0).getPwnd());
        assertEquals(16.5, students.get(0).getIrs());
        assertEquals(17.5, students.get(0).getStages7());
        assertEquals(18.5, students.get(0).getS5());
        assertEquals(19.5, students.get(0).getS6());
    }

    @Test
    void shouldThrowExceptionWhenFileReadingFails() throws Exception {
        when(file.getInputStream()).thenThrow(IOException.class);

        Exception exception = assertThrows(IOException.class, () -> csvProcessingService.processCsvFile(file));

        String expectedMessage = "Error while processing CSV file: ";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}

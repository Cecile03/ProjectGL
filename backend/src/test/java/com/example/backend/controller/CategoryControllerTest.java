package com.example.backend.controller;

import com.example.backend.model.Category;
import com.example.backend.service.CategoryService;
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

class CategoryControllerTest {

    @InjectMocks
    CategoryController categoryController;

    @Mock
    CategoryService categoryService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateCategory() {
        Category newCategoryData = new Category();
        int id = 1;

        ResponseEntity<Void> response = categoryController.update(id, newCategoryData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(categoryService, times(1)).update(id, newCategoryData);
    }

    @Test
    void update_shouldReturnInternalServerErrorWhenExceptionThrown() {
        // Setup
        int id = 1;
        Category newCategoryData = new Category();
        doThrow(new RuntimeException()).when(categoryService).update(id, newCategoryData);

        // Execution
        ResponseEntity<Void> response = categoryController.update(id, newCategoryData);

        // Verification
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categoryService, times(1)).update(id, newCategoryData);
    }

    @Test
    void deleteCategory() {
        int id = 1;

        ResponseEntity<Void> response = categoryController.delete(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(categoryService, times(1)).delete(id);
    }

    @Test
    void delete_shouldReturnInternalServerErrorWhenExceptionThrown() {
        // Setup
        int id = 1;
        doThrow(new RuntimeException()).when(categoryService).delete(id);

        // Execution
        ResponseEntity<Void> response = categoryController.delete(id);

        // Verification
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categoryService, times(1)).delete(id);
    }

    @Test
    void getCategoryById() {
        int id = 1;
        Category category = new Category();
        when(categoryService.getCategoryById(id)).thenReturn(category);

        Category response = categoryController.getCategoryById(id);

        assertEquals(category, response);
    }

    @Test
    void getAllCategories() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryService.getAll()).thenReturn(categories);

        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
    }

    @Test
    void getAllCategories_shouldReturnInternalServerErrorWhenExceptionThrown() {
        // Setup
        when(categoryService.getAll()).thenThrow(new RuntimeException());

        // Execution
        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        // Verification
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categoryService, times(1)).getAll();
    }

    @Test
    void createCategory_shouldReturnOkWhenNoExceptionThrown() {
        // Setup
        Category category = new Category();
        doNothing().when(categoryService).createCategory(category);

        // Execution
        ResponseEntity<Void> response = categoryController.createCategory(category);

        // Verification
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(categoryService, times(1)).createCategory(category);
    }

    @Test
    void createCategory_shouldReturnBadRequestWhenExceptionThrown() {
        // Setup
        Category category = new Category();
        doThrow(new RuntimeException()).when(categoryService).createCategory(category);

        // Execution
        ResponseEntity<Void> response = categoryController.createCategory(category);

        // Verification
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(categoryService, times(1)).createCategory(category);
    }

    @Test
    void getCategoriesByGradeScaleId_shouldReturnOkWhenNoExceptionThrown() {
        // Setup
        int gradeScaleId = 1;
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryService.getCategoriesByGradeScaleId(gradeScaleId)).thenReturn(categories);

        // Execution
        ResponseEntity<List<Category>> response = categoryController.getCategoriesByGradeScaleId(gradeScaleId);

        // Verification
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
        verify(categoryService, times(1)).getCategoriesByGradeScaleId(gradeScaleId);
    }

    @Test
    void getCategoriesByGradeScaleId_shouldReturnInternalServerErrorWhenExceptionThrown() {
        // Setup
        int gradeScaleId = 1;
        when(categoryService.getCategoriesByGradeScaleId(gradeScaleId)).thenThrow(new RuntimeException());

        // Execution
        ResponseEntity<List<Category>> response = categoryController.getCategoriesByGradeScaleId(gradeScaleId);

        // Verification
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categoryService, times(1)).getCategoriesByGradeScaleId(gradeScaleId);
    }

    @Test
    void deleteAll_shouldReturnOkWhenNoExceptionThrown() {
        // Setup
        doNothing().when(categoryService).deleteAll();

        // Execution
        ResponseEntity<Void> response = categoryController.deleteAll();

        // Verification
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(categoryService, times(1)).deleteAll();
    }

    @Test
    void deleteAll_shouldReturnInternalServerErrorWhenExceptionThrown() {
        // Setup
        doThrow(new RuntimeException()).when(categoryService).deleteAll();

        // Execution
        ResponseEntity<Void> response = categoryController.deleteAll();

        // Verification
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categoryService, times(1)).deleteAll();
    }
}

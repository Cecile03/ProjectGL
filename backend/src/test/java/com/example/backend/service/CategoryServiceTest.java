package com.example.backend.service;

import com.example.backend.dao.GradeScaleDao;
import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.Category;
import com.example.backend.dao.CategoryDao;
import com.example.backend.model.GradeScale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    DetailService detailService;

    @Mock
    CategoryDao categoryDao;

    @Mock
    GradeScaleDao gradeScaleDao;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCategoryById() {
        int id = 1;
        Category category = new Category();
        when(categoryDao.findById(id)).thenReturn(Optional.of(category));

        Category response = categoryService.getCategoryById(id);

        assertEquals(category, response);
    }

    @Test
    void addCategory() {
        Category category = new Category();

        categoryService.createCategory(category);

        verify(categoryDao, times(1)).save(category);
    }

    @Test
    void updateCategory() {
        int id = 1;
        Category oldCategory = new Category();
        Category newCategoryData = new Category();
        when(categoryDao.findById(id)).thenReturn(Optional.of(oldCategory));

        categoryService.update(id, newCategoryData);

        verify(categoryDao, times(1)).save(oldCategory);
    }

    @Test
    void deleteCategory() {
        int id = 1;
        Category category = new Category();
        when(categoryDao.findById(id)).thenReturn(Optional.of(category));

        categoryService.delete(id);

        verify(categoryDao, times(1)).deleteById(id);
    }

    @Test
    void testGetAll() {
        // Given
        Category category1 = new Category();
        Category category2 = new Category();
        List<Category> expectedCategories = Arrays.asList(category1, category2);

        // Mocking the CategoryDao
        when(categoryDao.findAll()).thenReturn(expectedCategories);

        // When
        List<Category> actualCategories = categoryService.getAll();

        // Then
        assertEquals(expectedCategories, actualCategories);
    }

    @Test
    void testGetCategoriesByGradeScaleId() {
        // Given
        int gradeScaleId = 1;
        Category category1 = new Category();
        Category category2 = new Category();
        List<Category> expectedCategories = Arrays.asList(category1, category2);

        // Mocking the CategoryDao
        when(categoryDao.findByGradeScaleId(gradeScaleId)).thenReturn(expectedCategories);

        // When
        List<Category> actualCategories = categoryService.getCategoriesByGradeScaleId(gradeScaleId);

        // Then
        assertEquals(expectedCategories, actualCategories);
    }

    @Test
    void testDeleteAll() {
        // When
        categoryService.deleteAll();

        // Then
        verify(categoryDao, times(1)).deleteAll();
        verify(detailService, times(1)).deleteAll();
    }

    @Test
    void testFromDTO() {
        // Given
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Test Category");
        categoryDTO.setGradeScaleId(1);

        GradeScale gradeScale = new GradeScale();
        when(gradeScaleDao.findById(1)).thenReturn(Optional.of(gradeScale));

        // When
        Category category = categoryService.fromDTO(categoryDTO);

        // Then
        assertEquals(categoryDTO.getId(), category.getId());
        assertEquals(categoryDTO.getName(), category.getName());
        assertEquals(gradeScale, category.getGradeScale());
    }
}

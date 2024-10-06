package com.example.backend.service;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.DetailDTO;
import com.example.backend.dto.GradeScaleDTO;
import com.example.backend.model.Category;
import com.example.backend.model.Detail;
import com.example.backend.model.GradeScale;
import com.example.backend.dao.GradeScaleDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GradeScaleServiceTest {

    @InjectMocks
    GradeScaleService gradeScaleService;

    @Mock
    GradeScaleDao gradeScaleDao;

    @Mock
    CategoryService categoryService;

    @Mock
    DetailService detailService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getGradeScaleById() {
        int id = 1;
        GradeScale gradeScale = new GradeScale();
        when(gradeScaleDao.findById(id)).thenReturn(Optional.of(gradeScale));

        GradeScale response = gradeScaleService.getGradeScaleById(id);

        assertEquals(gradeScale, response);
    }

    @Test
    void addGradeScale() {
        GradeScale gradeScale = new GradeScale();

        gradeScaleService.createGradeScale(gradeScale);

        verify(gradeScaleDao, times(1)).save(gradeScale);
    }

    @Test
    void updateGradeScale() {
        int id = 1;
        GradeScale oldGradeScale = new GradeScale();
        GradeScale newGradeScaleData = new GradeScale();
        when(gradeScaleDao.findById(id)).thenReturn(Optional.of(oldGradeScale));

        gradeScaleService.update(id, newGradeScaleData);

        verify(gradeScaleDao, times(1)).save(oldGradeScale);
    }

    @Test
    void deleteGradeScale() {
        int id = 1;

        gradeScaleService.delete(id);

        verify(gradeScaleDao, times(1)).deleteById(id);
    }

    @Test
    void getAllGradeScales() {
        List<GradeScale> gradeScales = Arrays.asList(new GradeScale(), new GradeScale());
        when(gradeScaleDao.findAll()).thenReturn(gradeScales);

        List<GradeScale> response = gradeScaleService.getAll();

        assertEquals(gradeScales, response);
    }

    @Test
    void shouldDeleteAllGradeScalesAndCategories() {
        gradeScaleService.deleteAll();
        verify(gradeScaleDao, times(1)).deleteAll();
        verify(categoryService, times(1)).deleteAll();
    }

    @Test
    void shouldConvertFromDTO() {
        GradeScaleDTO gradeScaleDTO = new GradeScaleDTO();
        gradeScaleDTO.setName("Test Name");
        gradeScaleDTO.setDescription("Test Description");

        GradeScale gradeScale = gradeScaleService.fromDTO(gradeScaleDTO);

        assertEquals(gradeScaleDTO.getName(), gradeScale.getName());
        assertEquals(gradeScaleDTO.getDescription(), gradeScale.getDescription());
    }

    @Test
    void shouldCreateFullGradeScales() {
        GradeScaleDTO gradeScaleDTO = new GradeScaleDTO();
        gradeScaleDTO.setName("Test Name");
        gradeScaleDTO.setDescription("Test Description");
        gradeScaleDTO.setCategories(new ArrayList<>());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        categoryDTO.setDetails(new ArrayList<>());

        DetailDTO detailDTO = new DetailDTO();
        detailDTO.setName("Test Detail");
        detailDTO.setDescription("Test Description");
        detailDTO.setMark(5);

        categoryDTO.getDetails().add(detailDTO);
        gradeScaleDTO.getCategories().add(categoryDTO);

        List<GradeScaleDTO> gradeScalesDTO = new ArrayList<>();
        gradeScalesDTO.add(gradeScaleDTO);

        // Add mock for categoryService.fromDTO
        Category category = new Category();
        when(categoryService.fromDTO(any(CategoryDTO.class))).thenReturn(category);

        // Add mock for detailService.fromDTO
        Detail detail = new Detail();
        when(detailService.fromDTO(any(DetailDTO.class))).thenReturn(detail);

        gradeScaleService.createFullGradeScales(gradeScalesDTO);

        verify(gradeScaleDao, times(1)).deleteAll();
        verify(categoryService, times(1)).deleteAll();
        verify(gradeScaleDao, times(1)).save(any(GradeScale.class));
        verify(categoryService, times(1)).createCategory(any(Category.class));
        verify(detailService, times(1)).createDetail(any(Detail.class));
    }
}

package com.example.backend.service;

import com.example.backend.dao.CategoryDao;
import com.example.backend.dao.TeamGradeDao;
import com.example.backend.dto.DetailDTO;
import com.example.backend.model.Category;
import com.example.backend.model.Detail;
import com.example.backend.dao.DetailDao;
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

class DetailServiceTest {

    @InjectMocks
    DetailService detailService;

    @Mock
    DetailDao detailDao;

    @Mock
    CategoryDao categoryDao;

    @Mock
    TeamGradeDao teamGradeDao;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDetailById() {
        int id = 1;
        Detail detail = new Detail();
        when(detailDao.findById(id)).thenReturn(Optional.of(detail));

        Detail response = detailService.getDetailById(id);

        assertEquals(detail, response);
    }

    @Test
    void addDetail() {
        Detail detail = new Detail();

        detailService.createDetail(detail);

        verify(detailDao, times(1)).save(detail);
    }

    @Test
    void updateDetail() {
        int id = 1;
        Detail oldDetail = new Detail();
        Detail newDetailData = new Detail();
        when(detailDao.findById(id)).thenReturn(Optional.of(oldDetail));

        Detail response = detailService.update(id, newDetailData);

        assertEquals(oldDetail, response);
        verify(detailDao, times(1)).save(oldDetail);
    }

    @Test
    void deleteDetail() {
        int id = 1;

        detailService.delete(id);

        verify(detailDao, times(1)).deleteById(id);
    }

    @Test
    void getAllDetails() {
        List<Detail> details = Arrays.asList(new Detail(), new Detail());
        when(detailDao.findAll()).thenReturn(details);

        List<Detail> response = detailService.getAll();

        assertEquals(details, response);
    }

    @Test
    void shouldReturnDetailsByCategoryId() {
        int categoryId = 1;
        Detail detail1 = new Detail();
        Detail detail2 = new Detail();
        List<Detail> expectedDetails = Arrays.asList(detail1, detail2);
        when(detailDao.findByCategoryId(categoryId)).thenReturn(expectedDetails);

        List<Detail> actualDetails = detailService.getDetailsByCategoryId(categoryId);

        assertEquals(expectedDetails, actualDetails);
    }

    @Test
    void shouldDeleteAllDetails() {
        detailService.deleteAll();
        verify(detailDao, times(1)).deleteAll();
        verify(teamGradeDao, times(1)).deleteAll();
    }

    @Test
    void shouldConvertFromDTO() {
        DetailDTO detailDTO = new DetailDTO();
        detailDTO.setName("Test Name");
        detailDTO.setDescription("Test Description");
        detailDTO.setMark(5);
        detailDTO.setCategoryId(1);

        Category category = new Category();
        when(categoryDao.findById(detailDTO.getCategoryId())).thenReturn(Optional.of(category));

        Detail detail = detailService.fromDTO(detailDTO);

        assertEquals(detailDTO.getName(), detail.getName());
        assertEquals(detailDTO.getDescription(), detail.getDescription());
        assertEquals(detailDTO.getMark(), detail.getMark());
        assertEquals(category, detail.getCategory());
    }

}

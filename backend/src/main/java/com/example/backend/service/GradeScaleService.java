package com.example.backend.service;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.DetailDTO;
import com.example.backend.dto.GradeScaleDTO;
import com.example.backend.model.Category;
import com.example.backend.model.GradeScale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.backend.dao.GradeScaleDao;

import java.util.List;

/**
 * Service de gestion des échelles de notation.
 */
@Service
public class GradeScaleService {

    private final GradeScaleDao gradeScaleDao;
    private final CategoryService categoryService;
    private final DetailService detailService;

    /**
     * Constructeur de la classe GradeScaleService.
     *
     * @param gradeScaleDao Le DAO des échelles de notation.
     * @param categoryService Le service de gestion des catégories.
     * @param detailService Le service de gestion des détails.
     */
    @Autowired
    public GradeScaleService(GradeScaleDao gradeScaleDao, CategoryService categoryService, DetailService detailService) {
        this.gradeScaleDao = gradeScaleDao;
        this.categoryService = categoryService;
        this.detailService = detailService;
    }

    /**
     * Récupère une échelle de notation par son identifiant.
     *
     * @param id L'identifiant de l'échelle de notation.
     * @return L'échelle de notation créée.
     */
    public GradeScale getGradeScaleById(int id) {
        return gradeScaleDao.findById(id).orElse(null);
    }

    /**
     * Crée une échelle de notation.
     *
     * @param gradeScale L'échelle de notation à créer.
     */
    public void createGradeScale(GradeScale gradeScale) {
        gradeScaleDao.save(gradeScale);
    }

    /**
     * Met à jour une échelle de notation par son identifiant.
     *
     * @param id L'identifiant de l'échelle de notation.
     * @param newGradeScaleData Les nouvelles données de l'échelle de notation.
     */
    public void update(int id, GradeScale newGradeScaleData) {
        GradeScale gradeScale = gradeScaleDao.findById(id).orElseThrow(() -> new RuntimeException("Grade scale not found"));
        gradeScale.setName(newGradeScaleData.getName());
        gradeScale.setDescription(newGradeScaleData.getDescription());
        gradeScaleDao.save(gradeScale);
    }

    /**
     * Supprime une échelle de notation par son identifiant.
     *
     * @param id L'identifiant de l'échelle de notation.
     */
    public void delete(int id) {
        gradeScaleDao.deleteById(id);
    }

    /**
     * Récupère toutes les échelles de notation.
     *
     * @return La liste des échelles de notation.
     */
    public List<GradeScale> getAll() {
        return gradeScaleDao.findAll();
    }

    /**
     * Supprime toutes les échelles de notation.
     */
    public void deleteAll() {
        categoryService.deleteAll();
        gradeScaleDao.deleteAll();
    }

    /**
     * Crée une échelle de notation à partir d'un DTO.
     *
     * @param gradeScaleDTO Le DTO de l'échelle de notation.
     * @return L'échelle de notation créée.
     */
    public GradeScale fromDTO(GradeScaleDTO gradeScaleDTO) {
        GradeScale gradeScale = new GradeScale();
        gradeScale.setName(gradeScaleDTO.getName());
        gradeScale.setDescription(gradeScaleDTO.getDescription());
        return gradeScale;
    }

    /**
     * Crée des échelles de notation complètes à partir de DTO.
     *
     * @param gradeScalesDTO Les DTO des échelles de notation.
     */
    public void createFullGradeScales(List<GradeScaleDTO> gradeScalesDTO) {
        deleteAll();
        int cmptGradeScale = 1;
        int cmptCategory = 1;
        for(GradeScaleDTO gradeScaleDTO : gradeScalesDTO) {
            GradeScale gradeScale = fromDTO(gradeScaleDTO);
            gradeScale.setId(cmptGradeScale++);
            createGradeScale(gradeScale);
            List<CategoryDTO> categoriesDTO = gradeScaleDTO.getCategories();
            for (CategoryDTO categoryDTO : categoriesDTO) {
                categoryDTO.setGradeScaleId(gradeScale.getId());
                Category category = categoryService.fromDTO(categoryDTO);
                category.setId(cmptCategory++);
                categoryService.createCategory(category);
                List<DetailDTO> detailsDTO = categoryDTO.getDetails();
                for (DetailDTO detailDTO : detailsDTO) {
                    detailDTO.setCategoryId(category.getId());
                    detailService.createDetail(detailService.fromDTO(detailDTO));
                }
            }
        }
    }
}

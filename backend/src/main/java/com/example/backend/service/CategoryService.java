package com.example.backend.service;

import com.example.backend.dao.CategoryDao;
import com.example.backend.dao.GradeScaleDao;
import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de gestion des catégories.
 */
@Service
public class CategoryService {

    private final CategoryDao categoryDao;
    private final GradeScaleDao gradeScaleDao;
    private final DetailService detailService;

    /**
     * Constructeur de la classe CategoryService.
     *
     * @param categoryDao Le DAO des catégories.
     * @param gradeScaleDao Le DAO des échelles de notation.
     * @param detailService Le service de gestion des détails.
     */
    @Autowired
    public CategoryService(CategoryDao categoryDao, GradeScaleDao gradeScaleDao, DetailService detailService) {
        this.categoryDao = categoryDao;
        this.gradeScaleDao = gradeScaleDao;
        this.detailService = detailService;
    }

    /**
     * Récupère une catégorie par son identifiant.
     *
     * @param id L'identifiant de la catégorie.
     * @return La catégorie créée.
     */
    public Category getCategoryById(int id) {
        return categoryDao.findById(id).orElse(null);
    }

    /**
     * Met à jour une catégorie par son identifiant.
     *
     * @param id L'identifiant de la catégorie.
     * @param newCategoryData Les nouvelles données de la catégorie.
     */
    public void update(int id, Category newCategoryData) {
        Category category = categoryDao.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(newCategoryData.getName());
        category.setGradeScale(newCategoryData.getGradeScale());
        categoryDao.save(category);
    }

    /**
     * Supprime une catégorie par son identifiant.
     *
     * @param id L'identifiant de la catégorie.
     */
    public void delete(int id) {
        categoryDao.deleteById(id);
    }

    /**
     * Récupère toutes les catégories.
     *
     * @return La liste des catégories.
     */
    public List<Category> getAll() {
        return categoryDao.findAll();
    }

    /**
     * Récupère les catégories d'une échelle de notation.
     *
     * @param gradeScaleId L'identifiant de l'échelle de notation.
     * @return La liste des catégories de l'échelle de notation.
     */
    public List<Category> getCategoriesByGradeScaleId(int gradeScaleId) {
        return categoryDao.findByGradeScaleId(gradeScaleId);
    }

    /**
     * Supprime toutes les catégories.
     */
    public void deleteAll() {
        detailService.deleteAll();
        categoryDao.deleteAll();
    }

    /**
     * Crée une catégorie à partir d'un DTO.
     *
     * @param categoryDTO La catégorie DTO.
     * @return La catégorie créée.
     */
    public Category fromDTO(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        category.setGradeScale(gradeScaleDao.findById(categoryDTO.getGradeScaleId()).orElseThrow());
        return category;
    }

    /**
     * Crée une catégorie.
     *
     * @param category La catégorie.
     */
    public void createCategory(Category category) {
        categoryDao.save(category);
    }
}

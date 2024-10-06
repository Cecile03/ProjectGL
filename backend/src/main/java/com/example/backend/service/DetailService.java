package com.example.backend.service;

import com.example.backend.dao.CategoryDao;
import com.example.backend.dao.DetailDao;
import com.example.backend.dao.TeamGradeDao;
import com.example.backend.dto.DetailDTO;
import com.example.backend.model.Detail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de gestion des détails.
 */
@Service
public class DetailService {

    private final DetailDao detailDao;
    private final CategoryDao categoryDao;
    private final TeamGradeDao teamGradeDao;

    /**
     * Constructeur de la classe DetailService.
     *
     * @param detailDao Le DAO des détails.
     * @param categoryDao Le DAO des catégories.
     * @param teamGradeDao Le DAO des notes d'équipe.
     */
    @Autowired
    public DetailService(DetailDao detailDao, CategoryDao categoryDao, TeamGradeDao teamGradeDao) {
        this.detailDao = detailDao;
        this.categoryDao = categoryDao;
        this.teamGradeDao = teamGradeDao;
    }

    /**
     * Crée un détail.
     *
     * @param detail Le détail à créer.
     */
    public void createDetail(Detail detail) {
        detailDao.save(detail);
    }

    /**
     * Met à jour un détail.
     *
     * @param id L'identifiant du détail.
     * @param newDetailData Les nouvelles données du détail.
     * @return Le détail mis à jour.
     */
    public Detail update(int id, Detail newDetailData) {
        Detail detail = detailDao.findById(id).orElseThrow(()-> new RuntimeException("Detail not found"));
        detail.setName(newDetailData.getName());
        detail.setDescription(newDetailData.getDescription());
        detail.setMark(newDetailData.getMark());
        detail.setCategory(newDetailData.getCategory());
        detailDao.save(detail);
        return detail;
    }

    /**
     * Supprime un détail par son identifiant.
     *
     * @param id L'identifiant du détail.
     */
    public void delete(int id) {
        detailDao.deleteById(id);
    }

    /**
     * Récupère un détail par son identifiant.
     *
     * @param id L'identifiant du détail.
     * @return Le détail trouvé.
     */
    public Detail getDetailById(int id) {
        return detailDao.findById(id).orElse(null);
    }

    /**
     * Récupère tous les détails.
     *
     * @return La liste des détails.
     */
    public List<Detail> getAll() {
        return detailDao.findAll();
    }

    /**
     * Récupère les détails d'une catégorie.
     *
     * @param categoryId L'identifiant de la catégorie.
     * @return La liste des détails de la catégorie.
     */
    public List<Detail> getDetailsByCategoryId(int categoryId) {
        return detailDao.findByCategoryId(categoryId);
    }

    /**
     * Supprime tous les détails.
     */
    public void deleteAll() {
        teamGradeDao.deleteAll();
        detailDao.deleteAll();
    }

    /**
     * Crée un détail à partir d'un DTO.
     *
     * @param detailDTO Le DTO du détail.
     * @return Le détail créé.
     */
    public Detail fromDTO(DetailDTO detailDTO) {
        Detail detail = new Detail();
        detail.setName(detailDTO.getName());
        detail.setDescription(detailDTO.getDescription());
        detail.setMark(detailDTO.getMark());
        detail.setCategory(categoryDao.findById(detailDTO.getCategoryId()).orElseThrow());
        return detail;
    }
}

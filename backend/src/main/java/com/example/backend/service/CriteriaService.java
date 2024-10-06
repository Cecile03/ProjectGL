package com.example.backend.service;

import com.example.backend.dao.CriteriaDao;
import com.example.backend.model.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des critères.
 */
@Service
public class CriteriaService {

    private final CriteriaDao criteriaDao;

    /**
     * Constructeur de la classe CriteriaService.
     *
     * @param criteriaDao Le DAO des critères.
     */
    @Autowired
    public CriteriaService(CriteriaDao criteriaDao) {
        this.criteriaDao = criteriaDao;
    }

    /**
     * Sauvegarde un critère.
     *
     * @param criteria Le critère à sauvegarder.
     * @return Le critère sauvegardé.
     */
    public Criteria save(Criteria criteria) {
        if(criteria == null) {
            throw new IllegalArgumentException("Criteria is null");
        }
        if(criteriaDao.existsById(criteria.getId())) {
            throw new IllegalArgumentException("Criteria already exists");
        }
        return criteriaDao.save(criteria);
    }

    /**
     * Met à jour un critère.
     *
     * @param criteria Le critère à mettre à jour.
     * @return Le critère mis à jour.
     */
    public Criteria update(Criteria criteria) {
        if(criteria == null) {
            throw new IllegalArgumentException("Criteria is null");
        }
        if(!criteriaDao.existsById(criteria.getId())) {
            return this.save(criteria);
        }
        return criteriaDao.save(criteria);
    }

    /**
     * Récupère un critère par son identifiant.
     *
     * @param id L'identifiant du critère.
     * @return Le critère trouvé.
     */
    public Criteria findById(int id) {
        return criteriaDao.findById(id).orElseThrow();
    }

    /**
     * Supprime un critère.
     *
     * @param criteria Le critère à supprimer.
     */
    public void delete(Criteria criteria) {
        criteriaDao.delete(criteria);
    }

    /**
     * Supprime tous les critères.
     */
    public void deleteAll() {
        criteriaDao.deleteAll();
    }
}

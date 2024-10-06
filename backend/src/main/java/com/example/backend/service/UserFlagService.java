package com.example.backend.service;

import com.example.backend.dao.UserFlagDao;
import com.example.backend.model.UserFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de gestion des signalements d'utilisateurs.
 */
@Service
public class UserFlagService {

    private final UserFlagDao userFlagDao;

    /**
     * Constructeur de la classe UserFlagService.
     *
     * @param userFlagDao Le DAO des signalements d'utilisateurs.
     */
    @Autowired
    public UserFlagService(UserFlagDao userFlagDao) {
        this.userFlagDao = userFlagDao;
    }

    /**
     * Récupère tous les signalements d'utilisateurs.
     *
     * @return La liste des signalements d'utilisateurs.
     */
    public List<UserFlag> getAllUserFlags() {
        return userFlagDao.findAll();
    }

    /**
     * Enregistre un signalement d'utilisateur.
     *
     * @param userFlag Le signalement d'utilisateur.
     * @return Le signalement d'utilisateur enregistré.
     */
    public UserFlag saveUserFlag(UserFlag userFlag) {
        return userFlagDao.save(userFlag);
    }

    /**
     * Récupère tous les signalements d'utilisateurs par identifiant de signalement.
     * @param flagId L'identifiant du signalement.
     * @return La liste des signalements d'utilisateurs.
     */
    public List<UserFlag> getAllUserFlagByFlagId(int flagId) {
        return userFlagDao.findByFlag_Id(flagId);
    }

    /**
     * Met à jour le statut de validation d'un signalement d'utilisateur.
     *
     * @param id L'identifiant du signalement d'utilisateur.
     * @param validated Le statut de validation.
     * @return Le signalement d'utilisateur mis à jour.
     */
    public UserFlag setValidated(int id, boolean validated) {
        UserFlag userFlag = userFlagDao.findById(id)
                .orElseThrow(() -> new RuntimeException("UserFlag not found with id: " + id));
        userFlag.setValidated(validated);
        return userFlagDao.save(userFlag);
    }

    /**
     * Supprime tous les signalements d'utilisateurs par identifiant de signalement.
     * @param flagId L'identifiant du signalement.
     */
    public void deleteUserFlagsByFlagId(int flagId) {
        List<UserFlag> userFlags = userFlagDao.findByFlag_Id(flagId);
        userFlagDao.deleteAll(userFlags);
    }

    /**
     * Vérifie si tous les signalements d'utilisateurs sont validés par identifiant de signalement.
     * @param flagId L'identifiant du signalement.
     * @return true si tous les signalements d'utilisateurs sont validés, false sinon.
     */
    public boolean areAllUserFlagsValidatedByFlagId(int flagId) {
        List<UserFlag> userFlags = userFlagDao.findByFlag_Id(flagId);
        int validatedCount = 0;

        for (UserFlag userFlag : userFlags) {
            Boolean isValidated = userFlag.getValidated();
            if (Boolean.TRUE.equals(isValidated)) {
                validatedCount++;
            }
        }
        return validatedCount == userFlags.size();
    }
}
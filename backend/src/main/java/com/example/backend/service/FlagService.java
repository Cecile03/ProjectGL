package com.example.backend.service;

import com.example.backend.dao.FlagDao;
import com.example.backend.dto.FlagDto;
import com.example.backend.model.Flag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de gestion des drapeaux.
 */
@Service
public class FlagService {

    private final FlagDao flagDao;
    private final UserService userService;
    private final TeamService teamService;

    /**
     * Constructeur de la classe FlagService.
     *
     * @param flagDao Le DAO des drapeaux.
     * @param userService Le service de gestion des utilisateurs.
     * @param teamService Le service de gestion des équipes.
     */
    @Autowired
    public FlagService(FlagDao flagDao, UserService userService, TeamService teamService) {
        this.flagDao = flagDao;
        this.userService = userService;
        this.teamService = teamService;
    }

    /**
     * Récupère tous les drapeaux.
     *
     * @return La liste des drapeaux.
     */
    public List<Flag> getAllFlags() {
        return flagDao.findAll();
    }

    /**
     * Sauvegarde un drapeau.
     *
     * @param flag Le drapeau à sauvegarder.
     * @return Le drapeau sauvegardé.
     */
    public Flag saveFlag(Flag flag) {
        return flagDao.save(flag);
    }

    /**
     * Récupère un drapeau par son identifiant.
     *
     * @param id L'identifiant du drapeau.
     * @return Le drapeau créé.
     */
    public Flag getFlagById(int id) {
        return flagDao.findById(id).orElse(null);
    }

    /**
     * Crée un drapeau à partir d'un DTO.
     *
     * @param flagDto Le DTO du drapeau.
     * @return Le drapeau créé.
     */
    public Flag createFlag(FlagDto flagDto) {
        Flag flag = new Flag();
        flag.setId(flagDto.getId());

        flag.setUser(userService.getUserById(flagDto.getUserId()));
        flag.setTeam1(teamService.getTeamById(flagDto.getTeam1Id()));
        flag.setTeam2(teamService.getTeamById(flagDto.getTeam2Id()));
        flag.setComment(flagDto.getComment());

        return flagDao.save(flag);
    }

    /**
     * Supprime un drapeau par son identifiant.
     *
     * @param id L'identifiant du drapeau.
     */
    public void deleteFlagById(int id) {
        flagDao.deleteById(id);
    }
}
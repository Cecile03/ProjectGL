package com.example.backend.controller;

import com.example.backend.dto.FlagDto;
import com.example.backend.model.Flag;
import com.example.backend.service.FlagService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Le contrôleur Flag.
 * Gère les requêtes HTTP liées aux opérations de drapeau (Flag).
 */
@RestController
@RequestMapping("/flags")
@PreAuthorize("hasAnyAuthority('TC', 'SS', 'OL', 'PL','OS')")
public class FlagController {

    private final FlagService flagService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param flagService Le service de drapeau à injecter.
     */
    @Autowired
    public FlagController(FlagService flagService) {
        this.flagService = flagService;
    }

    /**
     * Gère les requêtes GET pour récupérer tous les drapeaux.
     *
     * @return La liste de tous les drapeaux.
     */
    @GetMapping()
    public List<Flag> getAllFlags() {
        return flagService.getAllFlags();
    }

    /**
     * Gère les requêtes GET pour récupérer un drapeau spécifique.
     *
     * @param id L'identifiant du drapeau à récupérer.
     * @return Le drapeau spécifié.
     */
    @GetMapping("/{id}")
    public Flag getFlagById(@PathVariable int id) {
        return flagService.getFlagById(id);
    }

    /**
     * Gère les requêtes POST pour créer un nouveau drapeau.
     *
     * @param flagDto Le DTO du drapeau à créer.
     * @return Le drapeau créé.
     */
    @PostMapping()
    public Flag createFlag(@RequestBody FlagDto flagDto) {
        return flagService.createFlag(flagDto);
    }

    /**
     * Gère les requêtes DELETE pour supprimer un drapeau spécifique.
     *
     * @param id L'identifiant du drapeau à supprimer.
     */
    @DeleteMapping("/{id}")
    public void deleteFlagById(@PathVariable int id) {
        flagService.deleteFlagById(id);
    }
    // Add more methods as needed
}
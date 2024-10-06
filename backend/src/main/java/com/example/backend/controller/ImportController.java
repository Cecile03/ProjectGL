package com.example.backend.controller;

import com.example.backend.dto.StudentDTO;
import com.example.backend.service.CsvProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Le contrôleur Import.
 * Gère les requêtes HTTP liées aux opérations d'importation.
 */
@RestController
@PreAuthorize("hasAnyAuthority('OL', 'PL')")
public class ImportController {

    private final CsvProcessingService csvProcessingService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param csvProcessingService Le service de traitement CSV à injecter.
     */
    @Autowired
    public ImportController(CsvProcessingService csvProcessingService) {
        this.csvProcessingService = csvProcessingService;
    }

    /**
     * Gère les requêtes POST pour télécharger un fichier.
     *
     * @param file Le fichier a téléchargé.
     * @return Une liste de DTO d'étudiants.
     */
    @PostMapping("/upload")
    public List<StudentDTO> handleFileUpload(@RequestParam("file") MultipartFile file) {
        // Traitement du fichier
        if (!file.isEmpty()) {
            try {
                return csvProcessingService.processCsvFile(file);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors du téléchargement du fichier " + file.getOriginalFilename() + ": " + e.getMessage(), e);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le fichier est vide");
        }
    }
}

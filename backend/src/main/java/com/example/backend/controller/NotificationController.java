package com.example.backend.controller;

import com.example.backend.dto.NotificationDTO;
import com.example.backend.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.backend.service.NotificationService;

import java.util.List;

/**
 * Le contrôleur Notification.
 * Gère les requêtes HTTP liées aux opérations de notification.
 */
@RestController
@RequestMapping("/notifications")
@PreAuthorize("hasAnyAuthority('TC', 'SS', 'OL', 'PL','OS')")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param notificationService Le service de notification à injecter.
     */
    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Gère les requêtes GET pour récupérer toutes les notifications.
     *
     * @return Une réponse contenant la liste de toutes les notifications.
     */
    @GetMapping()
    public ResponseEntity<List<Notification>> getAll() {
        try{
            return ResponseEntity.ok(notificationService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer toutes les notifications par ID utilisateur.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @return Une réponse contenant la liste de toutes les notifications pour un utilisateur spécifique.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getAllByUserId(@PathVariable int userId) {
        try{
            return ResponseEntity.ok(notificationService.getAllByReceiverId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes POST pour créer une notification.
     *
     * @param notificationDTO Le DTO de la notification à créer.
     * @return Une réponse contenant la notification créée.
     */
    @PostMapping()
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationDTO notificationDTO) {
        try{
            Notification notification = notificationService.fromDTOToObject(notificationDTO);
            return ResponseEntity.ok(notificationService.createNotification(notification));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes PUT pour mettre à jour une notification.
     *
     * @param notificationDTO Le DTO de la notification à mettre à jour.
     * @return Une réponse contenant la notification mise à jour.
     */
    @PutMapping()
    public ResponseEntity<Notification> updateNotification(@RequestBody NotificationDTO notificationDTO) {
        try{
            Notification notification = notificationService.fromDTOToObject(notificationDTO);
            return ResponseEntity.ok(notificationService.updateNotification(notification));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes PUT pour mettre à jour le statut d'une notification.
     *
     * @param id L'identifiant de la notification dont le statut doit être mis à jour.
     * @return Une réponse contenant la notification avec le statut mis à jour.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotificationStatus(@PathVariable int id) {
        try{
            return ResponseEntity.ok(notificationService.updateNotificationStatus(id));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer une notification.
     *
     * @param id L'identifiant de la notification à supprimer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Notification> deleteNotification(@PathVariable int id) {
        try{
            Notification notification = notificationService.getNotificationById(id);
            notificationService.deleteNotification(notification);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }
}

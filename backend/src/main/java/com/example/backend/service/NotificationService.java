package com.example.backend.service;

import com.example.backend.dao.NotificationDao;
import com.example.backend.dao.UserDao;
import com.example.backend.dto.NotificationDTO;
import com.example.backend.model.Notification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des notifications.
 */
@Service
public class NotificationService {

    private final NotificationDao notificationDao;
    private final UserDao userDao;

    /**
     * Constructeur de la classe NotificationService.
     *
     * @param notificationDao Le DAO des notifications.
     * @param userDao Le DAO des utilisateurs.
     */
    @Autowired
    public NotificationService(NotificationDao notificationDao, UserDao userDao) {
        this.notificationDao = notificationDao;
        this.userDao = userDao;
    }

    /**
     * Récupère toutes les notifications.
     *
     * @return La liste des notifications.
     */
    public List<Notification> getAll() {
        return notificationDao.findAll();
    }

    /**
     * Crée une notification.
     *
     * @param notification La notification à créer.
     * @return La notification créée.
     */
    public Notification createNotification(Notification notification) {
        notificationDao.save(notification);
        return notification;
    }

    /**
     * Supprime une notification.
     *
     * @param notification La notification à supprimer.
     */
    public void deleteNotification(Notification notification) {
        notificationDao.delete(notification);
    }

    /**
     * Supprime toutes les notifications d'un utilisateur.
     *
     * @param userId L'identifiant de l'utilisateur.
     */
    public void deleteAllNotificationsOfUser(int userId) {
        List<Notification> notifications = this.getAllByUserId(userId);
        notificationDao.deleteAll(notifications);
    }

    /**
     * Met à jour une notification.
     *
     * @param notification La notification à mettre à jour.
     * @return La notification mise à jour.
     */
    public Notification updateNotification(Notification notification) {
        return notificationDao.save(notification);
    }

    /**
     * Met à jour le statut d'une notification.
     *
     * @param id L'identifiant de la notification.
     * @return La notification mise à jour.
     */
    public Notification updateNotificationStatus(int id) {
        Optional<Notification> notificationOpt = notificationDao.findById(id);

        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setStatus(notification.getStatus() == Notification.Status.READ ? Notification.Status.UNREAD : Notification.Status.READ);
            notificationDao.save(notification);
            return notification;
        } else {
            // Gérer le cas où la notification n'a pas été trouvée
            throw new EntityNotFoundException("Notification with id " + id + " not found");
        }
    }

    /**
     * Récupère toutes les notifications d'un receveur.
     *
     * @param userId L'identifiant du receveur.
     * @return La liste des notifications du receveur.
     */
    public List<Notification> getAllByReceiverId(int userId) {
        return notificationDao.findByReceiverId(userId);
    }

    /**
     * Récupère toutes les notifications d'un émetteur.
     *
     * @param userId L'identifiant de l'émetteur.
     * @return La liste des notifications de l'émetteur.
     */
    public List<Notification> getAllByEmitterId(int userId) {
        return notificationDao.findByEmitterId(userId);
    }

    /**
     * Récupère toutes les notifications d'un utilisateur.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @return La liste des notifications de l'utilisateur.
     */
    public List<Notification> getAllByUserId(int userId) {
        List<Notification> notifications = this.getAllByReceiverId(userId);
        notifications.addAll(this.getAllByEmitterId(userId));
        return notifications;
    }

    /**
     * Convertit une notification DTO en objet Notification.
     *
     * @param notificationDTO La notification DTO.
     * @return La notification.
     */
    public Notification fromDTOToObject(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setId(notificationDTO.getId());
        notification.setType(notificationDTO.getType());
        notification.setStatus(notificationDTO.getStatus());
        notification.setDescription(notificationDTO.getDescription());
        notification.setGroupId(notificationDTO.getGroupId());
        notification.setEmitter(userDao.findById(notificationDTO.getEmitterId()).orElseThrow());
        notification.setReceiver(userDao.findById(notificationDTO.getReceiverId()).orElseThrow());
        notification.setDate(notificationDTO.getDate());
        return notification;
    }

    /**
     * Récupère une notification par son identifiant.
     *
     * @param id L'identifiant de la notification.
     * @return La notification.
     */
    public Notification getNotificationById(int id) {
        return notificationDao.findById(id).orElseThrow();
    }
}

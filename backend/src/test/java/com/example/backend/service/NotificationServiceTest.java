package com.example.backend.service;

import com.example.backend.dao.NotificationDao;
import com.example.backend.dao.UserDao;
import com.example.backend.dto.NotificationDTO;
import com.example.backend.dto.UserInteract;
import com.example.backend.model.Notification;
import com.example.backend.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationDao notificationDao;

    @Mock
    private UserDao userDao;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        Notification notification = new Notification();
        when(notificationDao.findAll()).thenReturn(List.of(notification));
        List<Notification> result = notificationService.getAll();
        assertEquals(1, result.size());
        verify(notificationDao, times(1)).findAll();
    }

    @Test
    void testCreateNotification() {
        Notification notification = new Notification();
        when(notificationDao.save(any(Notification.class))).thenReturn(notification);
        Notification result = notificationService.createNotification(notification);
        assertEquals(notification, result);
        verify(notificationDao, times(1)).save(any(Notification.class));
    }

    @Test
    void testDeleteNotification() {
        Notification notification = new Notification();
        doNothing().when(notificationDao).delete(any(Notification.class));
        notificationService.deleteNotification(notification);
        verify(notificationDao, times(1)).delete(any(Notification.class));
    }

    @Test
    void testUpdateNotification() {
        Notification notification = new Notification();
        when(notificationDao.save(any(Notification.class))).thenReturn(notification);
        Notification result = notificationService.updateNotification(notification);
        assertEquals(notification, result);
        verify(notificationDao, times(1)).save(any(Notification.class));
    }

    @Test
    void testGetNotificationById() {
        Notification notification = new Notification();
        when(notificationDao.findById(anyInt())).thenReturn(Optional.of(notification));
        Notification result = notificationService.getNotificationById(1);
        assertEquals(notification, result);
        verify(notificationDao, times(1)).findById(anyInt());
    }

    @Test
    void testGetNotificationByIdNotFound() {
        when(notificationDao.findById(anyInt())).thenThrow(new RuntimeException("Notification not found"));
        assertThrows(RuntimeException.class, () -> notificationService.getNotificationById(1));
    }

    @Test
    void shouldDeleteAllNotificationsOfUser() {
        Notification notification = new Notification();
        List<Notification> notifications = new ArrayList<>();
        notifications.add(notification);
        when(notificationDao.findByReceiverId(anyInt())).thenReturn(notifications);
        when(notificationDao.findByEmitterId(anyInt())).thenReturn(notifications);
        doNothing().when(notificationDao).deleteAll(notifications);
        notificationService.deleteAllNotificationsOfUser(1);
        verify(notificationDao, times(1)).deleteAll(notifications);
    }

    @Test
    void testFromDTOToObject() {
        // Arrange
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(1);
        notificationDTO.setType("type");
        notificationDTO.setStatus(Notification.Status.READ);
        notificationDTO.setDescription("description");
        notificationDTO.setGroupId(1);
        UserInteract emitterDTO = new UserInteract();
        emitterDTO.setId(1);
        UserInteract receiverDTO = new UserInteract();
        receiverDTO.setId(2);
        notificationDTO.setEmitterId(emitterDTO.getId());
        notificationDTO.setReceiverId(receiverDTO.getId());
        notificationDTO.setDate(new Date(2024, 1, 1));

        User emitter = new User();
        emitter.setId(1);
        User receiver = new User();
        receiver.setId(2);

        when(userDao.findById(emitterDTO.getId())).thenReturn(Optional.of(emitter));
        when(userDao.findById(receiverDTO.getId())).thenReturn(Optional.of(receiver));

        // Act
        Notification notification = notificationService.fromDTOToObject(notificationDTO);

        // Assert
        assertEquals(notificationDTO.getId(), notification.getId());
        assertEquals(notificationDTO.getType(), notification.getType());
        assertEquals(notificationDTO.getStatus(), notification.getStatus());
        assertEquals(notificationDTO.getDescription(), notification.getDescription());
        assertEquals(notificationDTO.getGroupId(), notification.getGroupId());
        assertEquals(emitter.getId(), notification.getEmitter().getId());
        assertEquals(receiver.getId(), notification.getReceiver().getId());
        assertEquals(notificationDTO.getDate(), notification.getDate());

        verify(userDao, times(1)).findById(emitterDTO.getId());
        verify(userDao, times(1)).findById(receiverDTO.getId());
    }

    @Test
    void testUpdateNotificationStatus() {
        // Arrange
        Notification notification = new Notification();
        notification.setId(1);
        notification.setStatus(Notification.Status.UNREAD);

        when(notificationDao.findById(anyInt())).thenReturn(Optional.of(notification));

        // Act
        Notification result = notificationService.updateNotificationStatus(1);

        // Assert
        assertEquals(Notification.Status.READ, result.getStatus());
        verify(notificationDao, times(1)).findById(anyInt());
        verify(notificationDao, times(1)).save(any(Notification.class));
    }

    @Test
    void testUpdateNotificationStatus_NotificationNotFound() {
        // Arrange
        when(notificationDao.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> notificationService.updateNotificationStatus(1));
        verify(notificationDao, times(1)).findById(anyInt());
    }

}
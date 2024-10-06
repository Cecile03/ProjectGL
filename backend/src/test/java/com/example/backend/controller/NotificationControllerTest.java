package com.example.backend.controller;

import com.example.backend.dto.NotificationDTO;
import com.example.backend.model.Notification;
import com.example.backend.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll_shouldReturnNotificationsWhenSuccessful() {
        List<Notification> expectedNotifications = Collections.singletonList(new Notification());
        when(notificationService.getAll()).thenReturn(expectedNotifications);

        ResponseEntity<List<Notification>> response = notificationController.getAll();

        assertEquals(ResponseEntity.ok(expectedNotifications), response);
    }

    @Test
    void getAll_shouldReturnBadRequestWhenExceptionThrown() {
        when(notificationService.getAll()).thenThrow(new RuntimeException());

        ResponseEntity<List<Notification>> response = notificationController.getAll();

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void getAllByUserId_shouldReturnNotificationsWhenSuccessful() {
        int userId = 1;
        List<Notification> expectedNotifications = Collections.singletonList(new Notification());
        when(notificationService.getAllByReceiverId(userId)).thenReturn(expectedNotifications);

        ResponseEntity<List<Notification>> response = notificationController.getAllByUserId(userId);

        assertEquals(ResponseEntity.ok(expectedNotifications), response);
    }

    @Test
    void getAllByUserId_shouldReturnBadRequestWhenExceptionThrown() {
        int userId = 1;
        when(notificationService.getAllByReceiverId(userId)).thenThrow(new RuntimeException());

        ResponseEntity<List<Notification>> response = notificationController.getAllByUserId(userId);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void createNotification_shouldReturnNotificationWhenSuccessful() {
        NotificationDTO notificationDTO = new NotificationDTO();
        Notification expectedNotification = new Notification();
        when(notificationService.fromDTOToObject(notificationDTO)).thenReturn(expectedNotification);
        when(notificationService.createNotification(expectedNotification)).thenReturn(expectedNotification);

        ResponseEntity<Notification> response = notificationController.createNotification(notificationDTO);

        assertEquals(ResponseEntity.ok(expectedNotification), response);
    }

    @Test
    void createNotification_shouldReturnBadRequestWhenExceptionThrown() {
        NotificationDTO notificationDTO = new NotificationDTO();
        when(notificationService.fromDTOToObject(notificationDTO)).thenThrow(new RuntimeException());

        ResponseEntity<Notification> response = notificationController.createNotification(notificationDTO);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void updateNotification_shouldReturnNotificationWhenSuccessful() {
        NotificationDTO notificationDTO = new NotificationDTO();
        Notification expectedNotification = new Notification();
        when(notificationService.fromDTOToObject(notificationDTO)).thenReturn(expectedNotification);
        when(notificationService.updateNotification(expectedNotification)).thenReturn(expectedNotification);

        ResponseEntity<Notification> response = notificationController.updateNotification(notificationDTO);

        assertEquals(ResponseEntity.ok(expectedNotification), response);
    }

    @Test
    void updateNotification_shouldReturnBadRequestWhenExceptionThrown() {
        NotificationDTO notificationDTO = new NotificationDTO();
        when(notificationService.fromDTOToObject(notificationDTO)).thenThrow(new RuntimeException());

        ResponseEntity<Notification> response = notificationController.updateNotification(notificationDTO);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void deleteNotification_shouldReturnOkWhenSuccessful() {
        int id = 1;
        Notification expectedNotification = new Notification();
        when(notificationService.getNotificationById(id)).thenReturn(expectedNotification);
        doNothing().when(notificationService).deleteNotification(expectedNotification);

        ResponseEntity<Notification> response = notificationController.deleteNotification(id);

        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void deleteNotification_shouldReturnBadRequestWhenExceptionThrown() {
        int id = 1;
        when(notificationService.getNotificationById(id)).thenThrow(new RuntimeException());

        ResponseEntity<Notification> response = notificationController.deleteNotification(id);

        assertEquals(ResponseEntity.status(400).build(), response);
    }

    @Test
    void updateNotificationStatus_shouldReturnNotificationWhenSuccessful() {
        // Arrange
        int id = 1;
        Notification expectedNotification = new Notification();
        when(notificationService.updateNotificationStatus(id)).thenReturn(expectedNotification);

        // Act
        ResponseEntity<Notification> response = notificationController.updateNotificationStatus(id);

        // Assert
        assertEquals(ResponseEntity.ok(expectedNotification), response);
    }

    @Test
    void updateNotificationStatus_shouldReturnBadRequestWhenExceptionThrown() {
        // Arrange
        int id = 1;
        when(notificationService.updateNotificationStatus(id)).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<Notification> response = notificationController.updateNotificationStatus(id);

        // Assert
        assertEquals(ResponseEntity.status(400).build(), response);
    }
}
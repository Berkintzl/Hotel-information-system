package web;

import model.Notification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.NotificationService;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/hotels/{hotelId}/notifications")
public class NotificationRestController {
    private final NotificationService notificationService;

    public NotificationRestController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Notification> list(@PathVariable int hotelId) throws SQLException {
        return notificationService.getNotificationsByHotelId(hotelId);
    }
}

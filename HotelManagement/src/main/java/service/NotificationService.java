package service;

import DAO.NotificationDAO;
import model.Notification;
import DAO.ReservationDAO;
import model.Reservation;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class NotificationService {
    private final NotificationDAO notificationDAO;
    private final ReservationDAO reservationDAO;

    public NotificationService(NotificationDAO notificationDAO, ReservationDAO reservationDAO) {
        this.notificationDAO = notificationDAO;
        this.reservationDAO = reservationDAO;
    }

    public List<Notification> getNotificationsByHotelId(int hotelId) throws SQLException {
        return notificationDAO.getNotificationsByHotelId(hotelId);
    }

    public void addNotification(Notification notification) throws SQLException {
        notificationDAO.createNotification(notification);
    }

    public void checkAndCreateExpiringReservationNotifications() throws SQLException {
        List<Reservation> reservations = reservationDAO.getExpiringReservations();

        for (Reservation reservation : reservations) {
            String message = "Reservation Number: " + reservation.getReservationNumber() + " is expiring soon." + "\n"+ "End Date: " +reservation.getEndDate();
            Notification notification = new Notification(0, reservation.getHotelId(), message, new Timestamp(System.currentTimeMillis()));
            addNotification(notification);
        }
    }
    public static class NotificationScheduler {
        private final NotificationService notificationService;

        public NotificationScheduler(NotificationService notificationService) {
            this.notificationService = notificationService;
        }

        public void start() {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        notificationService.checkAndCreateExpiringReservationNotifications();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 24 * 60 * 60 * 1000); // 24 saatte bir çalışır
        }
    }
}

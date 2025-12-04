package config;

import DAO.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import service.*;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    @Bean
    public RoomDAO roomDAO(DataSource dataSource) { return new RoomDAO(dataSource); }

    @Bean
    public ReservationDAO reservationDAO(DataSource dataSource) { return new ReservationDAO(dataSource); }

    @Bean
    public NotificationDAO notificationDAO(DataSource dataSource) { return new NotificationDAO(dataSource); }

    @Bean
    public ClientDAO clientDAO(DataSource dataSource) { return new ClientDAO(dataSource); }

    @Bean
    public ClientRatingDAO clientRatingDAO(DataSource dataSource) { return new ClientRatingDAO(dataSource); }

    @Bean
    public HotelDAO hotelDAO(DataSource dataSource) { return new HotelDAO(dataSource); }

    @Bean
    public UserDAO userDAO(DataSource dataSource) { return new UserDAO(dataSource); }

    @Bean
    public UserRoleDAO userRoleDAO(DataSource dataSource) { return new UserRoleDAO(dataSource); }

    @Bean
    public ReservationService reservationService(ReservationDAO reservationDAO, RoomDAO roomDAO) {
        return new ReservationService(reservationDAO, roomDAO);
    }

    @Bean
    public NotificationService notificationService(NotificationDAO notificationDAO, ReservationDAO reservationDAO) {
        return new NotificationService(notificationDAO, reservationDAO);
    }

    @Bean
    public ClientService clientService(ClientDAO clientDAO) { return new ClientService(clientDAO); }

    @Bean
    public ClientRatingService clientRatingService(ClientRatingDAO clientRatingDAO) { return new ClientRatingService(clientRatingDAO); }

    @Bean
    public HotelService hotelService(HotelDAO hotelDAO) { return new HotelService(hotelDAO); }

    @Bean
    public UserService userService(UserDAO userDAO, UserRoleDAO userRoleDAO, HotelDAO hotelDAO) {
        return new UserService(userDAO, userRoleDAO, hotelDAO);
    }

    @Bean
    public AuthenticationService authenticationService(UserDAO userDAO) { return new AuthenticationService(userDAO); }

    @Bean(initMethod = "start")
    public NotificationService.NotificationScheduler notificationScheduler(NotificationService notificationService) {
        return new NotificationService.NotificationScheduler(notificationService);
    }
}

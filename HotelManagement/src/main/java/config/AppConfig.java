package config;

import DAO.RoomDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    @Bean
    public RoomDAO roomDAO(DataSource dataSource) {
        return new RoomDAO(dataSource);
    }
}

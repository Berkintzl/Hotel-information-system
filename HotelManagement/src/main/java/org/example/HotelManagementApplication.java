package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.example", "config", "web", "DAO", "service"})
public class HotelManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelManagementApplication.class, args);
    }
}

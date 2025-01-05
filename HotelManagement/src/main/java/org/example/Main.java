package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.NotificationService;
import utils.DatabaseConnection;
import DAO.NotificationDAO;
import DAO.ReservationDAO;

import java.sql.Connection;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Hotel Management System Login");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Bildirimler için zamanlayıcı başlatma
            Connection connection = DatabaseConnection.getConnection();
            NotificationService notificationService = new NotificationService(new NotificationDAO(connection), new ReservationDAO(connection));
            NotificationService.NotificationScheduler scheduler = new NotificationService.NotificationScheduler(notificationService);
            scheduler.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load the FXML file. Check the file path.");
        }
    }
}

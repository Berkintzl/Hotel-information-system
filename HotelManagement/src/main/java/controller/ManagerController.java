package controller;

import DAO.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import service.ReservationService;
import service.UserService;
import service.NotificationService;
import org.example.SpringContextHolder;
import model.Reservation;
import model.User;
import model.Notification;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ManagerController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    @FXML
    private TableView<Reservation> reservationTableView;
    @FXML
    private TableColumn<Reservation, Integer> reservationClientIdColumn;
    @FXML
    private TableColumn<Reservation, String> reservationNumberColumn;
    @FXML
    private TableColumn<Reservation, String> roomNumberColumn;
    @FXML
    private TableColumn<Reservation, String> cancellationTypeColumn;
    @FXML
    private TableColumn<Reservation, String> roomCategoryColumn;
    @FXML
    private TableColumn<Reservation, java.util.Date> startDateColumn;
    @FXML
    private TableColumn<Reservation, java.util.Date> endDateColumn;
    @FXML
    private TableColumn<Reservation, Boolean> isCancelledColumn;

    @FXML
    private TableView<User> receptionistTableView;
    @FXML
    private TableColumn<User, String> receptionistUsernameColumn;
    @FXML
    private TableColumn<User, String> receptionistPasswordColumn;

    @FXML
    private TableView<Notification> notificationTableView;
    @FXML
    private TableColumn<Notification, String> notificationMessageColumn;
    @FXML
    private TableColumn<Notification, Timestamp> notificationCreatedAtColumn;

    private UserService userService;
    private ReservationService reservationService;
    private NotificationService notificationService;
    private ObservableList<Notification> notificationData = FXCollections.observableArrayList();
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadReservationData();
        loadReceptionistData();
        loadNotifications();
        checkForExpiringReservations();
    }

    public void initialize() {
        try {
            userService = SpringContextHolder.getBean(UserService.class);
            reservationService = SpringContextHolder.getBean(ReservationService.class);
            notificationService = SpringContextHolder.getBean(NotificationService.class);

            reservationClientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
            reservationNumberColumn.setCellValueFactory(new PropertyValueFactory<>("reservationNumber"));
            roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
            cancellationTypeColumn.setCellValueFactory(new PropertyValueFactory<>("cancellationType"));
            roomCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("roomCategory"));
            startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
            endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
            isCancelledColumn.setCellValueFactory(new PropertyValueFactory<>("isCancelled"));

            receptionistUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            receptionistPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

            notificationMessageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
            notificationCreatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            notificationTableView.setItems(notificationData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadReservationData() {
        if (currentUser == null) {
            return;
        }

        try {
            List<Reservation> reservations = reservationService.getReservationsByHotelId(currentUser.getHotelId());
            ObservableList<Reservation> reservationData = FXCollections.observableArrayList(reservations);
            reservationTableView.setItems(reservationData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkForExpiringReservations() {
        try {
            notificationService.checkAndCreateExpiringReservationNotifications();
            loadNotifications();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadReceptionistData() {
        if (currentUser == null) {
            return;
        }

        try {
            List<User> receptionists = userService.getReceptionistsByHotelId(currentUser.getHotelId());
            ObservableList<User> receptionistData = FXCollections.observableArrayList(receptionists);
            receptionistTableView.setItems(receptionistData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNotifications() {
        if (currentUser == null) {
            return;
        }

        try {
            List<Notification> notifications = notificationService.getNotificationsByHotelId(currentUser.getHotelId());
            notificationData.setAll(notifications);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCreateReceptionist() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        int hotelId = currentUser.getHotelId();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "All fields are required!");
            return;
        }

        try {
            userService.createReceptionist(username, password, hotelId);
            showAlert("Success", "Receptionist created successfully!");
            clearUsernameField();
            loadReceptionistData();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating the receptionist. Please try again.");
        }
    }

    @FXML
    public void handleGoBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while going back. Please try again.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void clearUsernameField(){
        usernameField.clear();
        passwordField.clear();
    }
}

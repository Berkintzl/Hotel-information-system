package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import service.ClientRatingService;
import service.ClientService;
import utils.DatabaseConnection;
import DAO.ClientRatingDAO;
import DAO.ClientDAO;
import model.Client;
import model.ClientRating;
import model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ClientRatingController {
    @FXML
    private TextField clientIdField;
    @FXML
    private TextField ratingField;
    @FXML
    private TableView<Client> clientTableView;
    @FXML
    private TableColumn<Client, Integer> clientIdColumn;
    @FXML
    private TableColumn<Client, String> clientNameColumn;
    @FXML
    private TableColumn<Client, String> clientEmailColumn;

    private ClientRatingService clientRatingService;
    private ClientService clientService;
    private ObservableList<Client> clientData = FXCollections.observableArrayList();
    private User currentUser; // Receptionist'in bilgileri

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadClientData();
    }

    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            clientRatingService = new ClientRatingService(new ClientRatingDAO(connection));
            clientService = new ClientService(new ClientDAO(connection));

            // TableView sütunlarını başlatma
            clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            clientEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

            // TableView verilerini başlatma
            clientTableView.setItems(clientData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadClientData() {
        if (currentUser == null) {
            return;
        }

        try {
            List<Client> clients = clientService.getClientsByHotelId(currentUser.getHotelId());
            clientData.setAll(clients);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCreateClientRating() {
        int clientId;
        int rating;

        try {
            clientId = Integer.parseInt(clientIdField.getText().trim());
            rating = Integer.parseInt(ratingField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Client ID and rating must be numbers!");
            return;
        }

        if (rating < 1 || rating > 5) {
            showAlert("Input Error", "Rating must be between 1 and 5!");
            return;
        }

        try {
            int hotelId = currentUser.getHotelId();
            ClientRating clientRating = new ClientRating(0, clientId, rating, hotelId);
            clientRatingService.addClientRating(clientRating);
            showAlert("Success", "Client rating created successfully!");
            clearclientIdField();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating the client rating. Please try again.");
        }
    }

    @FXML
    public void handleGoBack(ActionEvent event) {
        ReceptionistMainController.goBackToMain(event, currentUser);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void clearclientIdField(){
        clientIdField.clear();
        ratingField.clear();
    }
}

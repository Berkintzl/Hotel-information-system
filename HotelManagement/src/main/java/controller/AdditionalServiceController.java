package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import model.AdditionalService;
import model.User;
import service.AdditionalServiceService;
import org.example.SpringContextHolder;

import java.sql.SQLException;

public class AdditionalServiceController {
    @FXML
    private TextField serviceTypeField;
    @FXML
    private TextField seasonField;
    @FXML
    private TextField usageCountField;

    private AdditionalServiceService additionalServiceService;
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void initialize() {
        try {
            additionalServiceService = SpringContextHolder.getBean(AdditionalServiceService.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCreateAdditionalService() {
        String serviceType = serviceTypeField.getText().trim();
        String season = seasonField.getText().trim();
        int usageCount;

        try {
            usageCount = Integer.parseInt(usageCountField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Usage count must be a number!");
            return;
        }

        if (serviceType.isEmpty() || season.isEmpty()) {
            showAlert("Input Error", "All fields are required!");
            return;
        }

        try {
            if (currentUser != null) {
                int hotelId = currentUser.getHotelId();
                AdditionalService service = new AdditionalService(0, serviceType, season, usageCount, hotelId);
                additionalServiceService.addAdditionalService(service);
                showAlert("Success", "Additional service created successfully!");
                clearServiceTypeField();
            } else {
                showAlert("Error", "User information is missing. Please log in again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating the additional service. Please try again.");
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
    private void clearServiceTypeField() {
        serviceTypeField.clear();
        seasonField.clear();
    }
}

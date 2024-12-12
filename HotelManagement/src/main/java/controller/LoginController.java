package controller;

import model.User;
import DAO.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.AuthenticationService;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private AuthenticationService authService;

    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            authService = new AuthenticationService(new UserDAO(connection));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Username and password are required!");
            return;
        }

        try {
            User user = authService.authenticate(username, password);
            if (user != null) {
                Alert alert = showAlert("Login Successful", "Welcome, " + username + "!");
               clearusernameField();
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            openPanel(user);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                showAlert("Login Failed", "Invalid username or password.").showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Login Error", "An error occurred during login. Please try again later.").showAndWait();
        }
    }

    private void openPanel(User user) throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader;
        Parent root;

        switch (user.getRoleName()) {
            case "administrator":
                root = FXMLLoader.load(getClass().getResource("/fxml/admin_panel.fxml"));
                break;
            case "owner":
                loader = new FXMLLoader(getClass().getResource("/fxml/owner_panel.fxml"));
                root = loader.load();
                OwnerPanelController ownerController = loader.getController();
                ownerController.setCurrentUser(user);
                break;
            case "manager":
                loader = new FXMLLoader(getClass().getResource("/fxml/manager_panel.fxml"));
                root = loader.load();
                ManagerController managerController = loader.getController();
                managerController.setCurrentUser(user);
                break;
            case "receptionist":
                loader = new FXMLLoader(getClass().getResource("/fxml/receptionist_main_panel.fxml"));
                root = loader.load();
                ReceptionistMainController receptionistController = loader.getController();
                receptionistController.setCurrentUser(user);
                break;
            default:
                throw new Exception("Unknown role: " + user.getRoleName());
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private Alert showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert;
    }
    private void clearusernameField(){
        usernameField.clear();
        passwordField.clear();
    }
}

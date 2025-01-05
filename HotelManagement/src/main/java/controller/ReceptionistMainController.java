package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.User;

public class ReceptionistMainController {
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void handleOpenReceptionistPanel(ActionEvent event) {
        openPanel(event, "/fxml/receptionist_panel.fxml", currentUser);
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
    @FXML
    public void handleOpenAdditionalServicePanel(ActionEvent event) {
        openPanel(event, "/fxml/additional_service_panel.fxml", currentUser);
    }

    @FXML
    public void handleOpenClientRatingPanel(ActionEvent event) {
        openPanel(event, "/fxml/client_rating_panel.fxml", currentUser);
    }

    private void openPanel(ActionEvent event, String fxmlFilePath, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof AdditionalServiceController) {
                ((AdditionalServiceController) controller).setCurrentUser(user);
            } else if (controller instanceof ReceptionistPanelController) {
                ((ReceptionistPanelController) controller).setCurrentUser(user);
            } else if (controller instanceof ClientRatingController) {
                ((ClientRatingController) controller).setCurrentUser(user);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while opening the panel. Please try again.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static void goBackToMain(ActionEvent event, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(ReceptionistMainController.class.getResource("/fxml/receptionist_main_panel.fxml"));
            Parent root = loader.load();

            ReceptionistMainController controller = loader.getController();
            controller.setCurrentUser(user); // User bilgisini geçir

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while going back to the main panel. Please try again.");
            alert.showAndWait();
        }
    }

}

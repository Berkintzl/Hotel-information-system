package controller;

import DAO.RoomDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import model.Hotel;
import model.Room;
import service.HotelService;
import service.UserService;
import utils.DatabaseConnection;
import DAO.UserRoleDAO;
import DAO.UserDAO;
import DAO.HotelDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AdminController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField hotelNameField;
    @FXML
    private TextField hotelAddressField;
    @FXML private TextField roomNumberField;
    @FXML private ComboBox<Hotel> hotelComboBox;
    @FXML private ComboBox<String> roomCategoryComboBox;
    @FXML private ComboBox<Room> existingRoomsComboBox;

    private UserService userService;
    private HotelService hotelService;
    private RoomDAO roomDAO;

    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            userService = new UserService(new UserDAO(connection), new UserRoleDAO(connection), new HotelDAO(connection));
            hotelService = new HotelService(new HotelDAO(connection));
            roomDAO = new RoomDAO(connection);

            roomCategoryComboBox.setItems(FXCollections.observableArrayList(
                    "Standard",
                    "Deluxe",
                    "Suite",
                    "Family",
                    "Luxury",
                    "Single",
                    "Double",
                    "Twin",
                    "Presidential Suite",
                    "Economy"
            ));


            loadHotels();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error");
        }
    }

    private void loadHotels() {
        try {
            List<Hotel> hotels = hotelService.getAllHotels();
            hotelComboBox.setItems(FXCollections.observableArrayList(hotels));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load hotels");
        }
    }
    @FXML
    public void handleAddRoom() {
        Hotel selectedHotel = hotelComboBox.getValue();
        String roomNumber = roomNumberField.getText().trim();
        String roomCategory = roomCategoryComboBox.getValue();

        if (selectedHotel == null || roomNumber.isEmpty() || roomCategory == null) {
            showAlert("Input Error", "Please fill in all room details!");
            return;
        }

        try {
            Room existingRoom = roomDAO.getRoomByRoomNumber(roomNumber);
            if (existingRoom != null) {
                showAlert("Error", "Room number already exists!");
                return;
            }

            // Use RoomDAO to create room instead of direct SQL
            roomDAO.createRoom(roomNumber, roomCategory, selectedHotel.getId());

            showAlert("Success", "Room added successfully!");
            clearRoomFields();

            // Refresh the room list after adding new room
            handleHotelSelection();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add room");
        }
    }

    private void clearRoomFields() {
        roomNumberField.clear();
        roomCategoryComboBox.setValue(null);
    }
    @FXML
    public void handleCreateOwner() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String hotelName = hotelNameField.getText().trim();
        String hotelAddress = hotelAddressField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || hotelName.isEmpty() || hotelAddress.isEmpty()) {
            showAlert("Input Error", "All fields are required!");
            return;
        }

        try {
            int hotelId = hotelService.createHotel(hotelName, hotelAddress);
            userService.createUserWithRole(username, password, "owner", hotelId);
            showAlert("Success", "Hotel and owner created successfully!");
            clearUserNameField();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating the hotel and owner. Please try again.");
        }
    }
    @FXML
    public void handleHotelSelection() {
        Hotel selectedHotel = hotelComboBox.getValue();
        if (selectedHotel != null) {
            try {
                System.out.println("Selected Hotel ID: " + selectedHotel.getId()); // Debug için
                List<Room> rooms = roomDAO.getRoomsByHotelId(selectedHotel.getId());
                System.out.println("Found " + rooms.size() + " rooms"); // Debug için
                rooms.forEach(room -> System.out.println("Room: " + room.toString())); // Debug için
                existingRoomsComboBox.setItems(FXCollections.observableArrayList(rooms));
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load rooms for selected hotel");
            }
        } else {
            existingRoomsComboBox.getItems().clear();
        }
    }
    @FXML
    public void handleDeleteRoom() {
        Room selectedRoom = existingRoomsComboBox.getValue();
        if (selectedRoom == null) {
            showAlert("Error", "Please select a room to delete");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Room");
        confirmDialog.setContentText("Are you sure you want to delete room " + selectedRoom.getRoomNumber() + "?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                roomDAO.deleteRoom(selectedRoom.getRoomNumber());
                showAlert("Success", "Room deleted successfully");

                handleHotelSelection();

                existingRoomsComboBox.setValue(null);
            } catch (SQLException e) {
                String errorMessage = e.getMessage();
                if (errorMessage.contains("active reservations")) {
                    showAlert("Error", "Cannot delete room: It has active reservations");
                } else {
                    showAlert("Error", "Failed to delete room: " + errorMessage);
                    e.printStackTrace(); // Debug için
                }
            }
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
    private void clearUserNameField(){
        usernameField.clear();
        passwordField.clear();
        hotelNameField.clear();
        hotelAddressField.clear();

    }
}

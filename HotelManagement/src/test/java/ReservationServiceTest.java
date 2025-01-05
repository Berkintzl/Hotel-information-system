import DAO.ReservationDAO;
import DAO.RoomDAO;
import model.Reservation;
import model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ReservationService;
import utils.DatabaseConnection;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ReservationServiceTest {

    private ReservationService reservationService;
    private ReservationDAO reservationDAO;
    private RoomDAO roomDAO;

    @BeforeEach
    void setUp() throws SQLException {
        reservationDAO = new ReservationDAO(DatabaseConnection.getConnection());
        roomDAO = new RoomDAO(DatabaseConnection.getConnection());
        reservationService = new ReservationService(reservationDAO, roomDAO);
    }

    @Test
    void testListAllRooms() throws SQLException {
        // Get all rooms
        List<Room> allRooms = roomDAO.getAllRooms();
        System.out.println("Total number of rooms: " + allRooms.size());

        // Print room details
        for(Room room : allRooms) {
            System.out.println("Room No: " + room.getRoomNumber()
                    + ", Status: " + (room.getIsOccupied() ? "Occupied" : "Available")
                    + ", Hotel ID: " + room.getHotelId());
        }

        assertFalse(allRooms.isEmpty(), "No rooms found in database!");
    }

    @Test
    void testListAvailableRooms() throws SQLException {
        // Get available rooms
        List<Room> availableRooms = roomDAO.getAvailableRoomsByHotelId(1);
        System.out.println("Number of available rooms: " + availableRooms.size());

        // Print available room details
        for(Room room : availableRooms) {
            System.out.println("Available Room No: " + room.getRoomNumber()
                    + ", Category: " + room.getRoomCategory()
                    + ", Hotel ID: " + room.getHotelId());
        }
    }

    @Test
    void testGetReservationsByHotelId() throws SQLException {
        // Get reservations for a specific hotel
        List<Reservation> hotelReservations = reservationService.getReservationsByHotelId(1);

        // Print reservation details
        System.out.println("Number of hotel reservations: " + hotelReservations.size());
        for(Reservation res : hotelReservations) {
            System.out.println("Reservation No: " + res.getReservationNumber()
                    + ", Room No: " + res.getRoomNumber()
                    + ", Cancellation Status: " + (res.isCancelled() ? "Cancelled" : "Active"));
        }

        // Verify all reservations belong to the correct hotel
        for(Reservation res : hotelReservations) {
            assertEquals(1, res.getHotelId());
        }
    }

    @Test
    void testRoomCategoryValidation() throws SQLException {
        List<Room> rooms = roomDAO.getAllRooms();

        // Validate room categories
        for(Room room : rooms) {
            String category = room.getRoomCategory();
            assertNotNull(category, "Room category cannot be null");
            assertFalse(category.isEmpty(), "Room category cannot be empty");
        }
    }
}
package web;

import DAO.RoomDAO;
import model.Room;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/hotels/{hotelId}/rooms")
public class RoomRestController {
    private final RoomDAO roomDAO;

    public RoomRestController(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    @GetMapping("/available")
    public List<Room> available(@PathVariable int hotelId) throws SQLException {
        return roomDAO.getAvailableRoomsByHotelId(hotelId);
    }

    @GetMapping
    public List<Room> list(@PathVariable int hotelId) throws SQLException {
        return roomDAO.getRoomsByHotelId(hotelId);
    }

    public static class CreateRoomRequest {
        public String roomNumber;
        public String roomCategory;
    }

    @PostMapping
    public ResponseEntity<Void> create(@PathVariable int hotelId, @RequestBody CreateRoomRequest req) throws SQLException {
        Room existing = roomDAO.getRoomByRoomNumber(req.roomNumber);
        if (existing != null) {
            return ResponseEntity.status(409).build();
        }
        roomDAO.createRoom(req.roomNumber, req.roomCategory, hotelId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roomNumber}")
    public ResponseEntity<Void> delete(@PathVariable int hotelId, @PathVariable String roomNumber) throws SQLException {
        roomDAO.deleteRoom(roomNumber);
        return ResponseEntity.ok().build();
    }
}

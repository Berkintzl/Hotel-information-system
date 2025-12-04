package web;

import DAO.RoomDAO;
import model.Room;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.stream.Collectors;

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
    public List<RoomResponse> available(@PathVariable int hotelId) throws SQLException {
        List<Room> rooms = roomDAO.getAvailableRoomsByHotelId(hotelId);
        return rooms.stream().map(r -> {
            RoomResponse rr = new RoomResponse();
            rr.id = r.getId();
            rr.roomNumber = r.getRoomNumber();
            rr.roomCategory = r.getRoomCategory();
            rr.isOccupied = r.getIsOccupied();
            rr.reservationNumber = r.getReservationNumber();
            return rr;
        }).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping
    public List<RoomResponse> list(@PathVariable int hotelId) throws SQLException {
        List<Room> rooms = roomDAO.getRoomsByHotelId(hotelId);
        return rooms.stream().map(r -> {
            RoomResponse rr = new RoomResponse();
            rr.id = r.getId();
            rr.roomNumber = r.getRoomNumber();
            rr.roomCategory = r.getRoomCategory();
            rr.isOccupied = r.getIsOccupied();
            rr.reservationNumber = r.getReservationNumber();
            return rr;
        }).collect(Collectors.toList());
    }

    public static class CreateRoomRequest {
        @NotBlank
        public String roomNumber;
        @NotBlank
        public String roomCategory;
    }

    public static class RoomResponse {
        public int id;
        public String roomNumber;
        public String roomCategory;
        public boolean isOccupied;
        public String reservationNumber;
    }

    @PostMapping
    public ResponseEntity<Void> create(@PathVariable int hotelId, @Valid @RequestBody CreateRoomRequest req) throws SQLException {
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

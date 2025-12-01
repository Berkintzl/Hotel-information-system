package web;

import DAO.RoomDAO;
import model.Room;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

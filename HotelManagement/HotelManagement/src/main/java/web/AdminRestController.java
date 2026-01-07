package web;

import DAO.RoomDAO;
import DAO.HotelDAO;
import model.Room;
import model.Hotel;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final RoomDAO roomDAO;
    private final HotelDAO hotelDAO;

    public AdminRestController(RoomDAO roomDAO, HotelDAO hotelDAO) {
        this.roomDAO = roomDAO;
        this.hotelDAO = hotelDAO;
    }

    @GetMapping("/rooms")
    public List<RoomWithHotelResponse> allRooms() throws SQLException {
        List<Room> rooms = roomDAO.getAllRooms();
        List<Hotel> hotels = hotelDAO.getAllHotels();
        
        return rooms.stream().map(r -> {
            RoomWithHotelResponse rr = new RoomWithHotelResponse();
            rr.id = r.getId();
            rr.roomNumber = r.getRoomNumber();
            rr.roomCategory = r.getRoomCategory();
            rr.isOccupied = r.getIsOccupied();
            rr.hotelId = r.getHotelId();
            
            // Find hotel name
            Hotel hotel = hotels.stream()
                .filter(h -> h.getId() == r.getHotelId())
                .findFirst()
                .orElse(null);
            rr.hotelName = hotel != null ? hotel.getName() : "Unknown";
            
            return rr;
        }).collect(Collectors.toList());
    }

    public static class RoomWithHotelResponse {
        public int id;
        public String roomNumber;
        public String roomCategory;
        public boolean isOccupied;
        public int hotelId;
        public String hotelName;
    }
}

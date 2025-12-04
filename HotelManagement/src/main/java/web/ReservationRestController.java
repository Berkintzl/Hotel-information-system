package web;

import model.Reservation;
import model.Room;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ReservationService;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationRestController {
    private final ReservationService reservationService;

    public ReservationRestController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/hotels/{hotelId}/reservations")
    public List<Reservation> list(@PathVariable int hotelId) throws SQLException {
        return reservationService.getReservationsByHotelId(hotelId);
    }

    public static class CreateReservationRequest {
        public int clientId;
        public String reservationNumber;
        public String roomNumber;
        public String cancellationType;
        public String roomCategory;
        public Date startDate;
        public Date endDate;
        public int hotelId;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Void> create(@RequestBody CreateReservationRequest req) throws SQLException {
        Reservation r = new Reservation(0, req.clientId, req.reservationNumber, req.roomNumber, req.cancellationType, req.roomCategory, req.startDate, req.endDate, false, req.hotelId);
        reservationService.addReservation(r);
        return ResponseEntity.ok().build();
    }

    public static class CancelRequest {
        public String cancellationType;
    }

    @PostMapping("/reservations/{reservationNumber}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable String reservationNumber, @RequestBody CancelRequest req) throws SQLException {
        reservationService.cancelReservation(reservationNumber, req.cancellationType);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hotels/{hotelId}/rooms/available")
    public List<Room> availableRooms(@PathVariable int hotelId) throws SQLException {
        return reservationService.getAvailableRoomsByHotelId(hotelId);
    }
}

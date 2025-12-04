package web;

import model.Reservation;
import model.Room;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import service.ReservationService;
import java.util.stream.Collectors;

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
    public List<ReservationResponse> list(@PathVariable int hotelId) throws SQLException {
        List<Reservation> list = reservationService.getReservationsByHotelId(hotelId);
        return list.stream().map(r -> {
            ReservationResponse rr = new ReservationResponse();
            rr.reservationNumber = r.getReservationNumber();
            rr.roomNumber = r.getRoomNumber();
            rr.startDate = r.getStartDate();
            rr.endDate = r.getEndDate();
            rr.isCancelled = r.isCancelled();
            return rr;
        }).collect(Collectors.toList());
    }

    public static class CreateReservationRequest {
        @NotNull
        public Integer clientId;
        @NotBlank
        public String reservationNumber;
        @NotBlank
        public String roomNumber;
        public String cancellationType;
        @NotBlank
        public String roomCategory;
        @NotNull
        public Date startDate;
        @NotNull
        public Date endDate;
        @NotNull
        public Integer hotelId;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Void> create(@Valid @RequestBody CreateReservationRequest req) throws SQLException {
        Reservation r = new Reservation(0, req.clientId, req.reservationNumber, req.roomNumber, req.cancellationType, req.roomCategory, req.startDate, req.endDate, false, req.hotelId);
        reservationService.addReservation(r);
        return ResponseEntity.ok().build();
    }

    public static class CancelRequest {
        @NotBlank
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

    public static class ReservationResponse {
        public String reservationNumber;
        public String roomNumber;
        public Date startDate;
        public Date endDate;
        public boolean isCancelled;
    }
}

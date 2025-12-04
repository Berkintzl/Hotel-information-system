package web;

import model.ClientRating;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ClientRatingService;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/hotels/{hotelId}/ratings")
public class ClientRatingRestController {
    private final ClientRatingService clientRatingService;

    public ClientRatingRestController(ClientRatingService clientRatingService) {
        this.clientRatingService = clientRatingService;
    }

    @GetMapping
    public List<ClientRating> list(@PathVariable int hotelId) throws SQLException {
        return clientRatingService.getClientRatingsByHotelId(hotelId);
    }

    public static class CreateClientRatingRequest {
        public int clientId;
        public int rating;
    }

    @PostMapping
    public ResponseEntity<Void> create(@PathVariable int hotelId, @RequestBody CreateClientRatingRequest req) throws SQLException {
        clientRatingService.addClientRating(new ClientRating(0, req.clientId, req.rating, hotelId));
        return ResponseEntity.ok().build();
    }
}

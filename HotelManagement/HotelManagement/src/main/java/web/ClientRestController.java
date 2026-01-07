package web;

import model.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ClientService;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/hotels/{hotelId}/clients")
public class ClientRestController {
    private final ClientService clientService;

    public ClientRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<Client> list(@PathVariable int hotelId) throws SQLException {
        return clientService.getClientsByHotelId(hotelId);
    }

    public static class CreateClientRequest {
        public String name;
        public String email;
    }

    @PostMapping
    public ResponseEntity<Integer> create(@PathVariable int hotelId, @RequestBody CreateClientRequest req) throws SQLException {
        int id = clientService.addClient(new Client(0, req.name, req.email, hotelId));
        return ResponseEntity.ok(id);
    }
}

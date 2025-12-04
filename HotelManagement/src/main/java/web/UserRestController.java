package web;

import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hotels/{hotelId}/receptionists")
    public List<User> receptionists(@PathVariable int hotelId) throws SQLException {
        return userService.getReceptionistsByHotelId(hotelId);
    }

    public static class CreateReceptionistRequest {
        public String username;
        public String password;
    }

    @PostMapping("/hotels/{hotelId}/receptionists")
    public ResponseEntity<Void> createReceptionist(@PathVariable int hotelId, @RequestBody CreateReceptionistRequest req) throws SQLException {
        userService.createReceptionist(req.username, req.password, hotelId);
        return ResponseEntity.ok().build();
    }
}

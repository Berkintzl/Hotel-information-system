package web;

import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserResponse> allUsers() throws SQLException {
        List<User> users = userService.getAllUsers();
        return users.stream().map(u -> {
            UserResponse r = new UserResponse();
            r.id = u.getId();
            r.username = u.getUsername();
            r.roleName = u.getRoleName();
            r.hotelId = u.getHotelId();
            return r;
        }).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/hotels/{hotelId}/receptionists")
    public List<UserResponse> receptionists(@PathVariable int hotelId) throws SQLException {
        List<User> users = userService.getReceptionistsByHotelId(hotelId);
        return users.stream().map(u -> {
            UserResponse r = new UserResponse();
            r.id = u.getId();
            r.username = u.getUsername();
            r.roleName = u.getRoleName();
            r.hotelId = u.getHotelId();
            return r;
        }).collect(java.util.stream.Collectors.toList());
    }

    public static class CreateReceptionistRequest {
        @NotBlank
        public String username;
        @NotBlank
        public String password;
    }

    public static class CreateManagerRequest {
        @NotBlank
        public String username;
        @NotBlank
        public String password;
    }

    public static class UserResponse {
        public int id;
        public String username;
        public String roleName;
        public int hotelId;
    }

    @PostMapping("/hotels/{hotelId}/receptionists")
    public ResponseEntity<String> createReceptionist(@PathVariable int hotelId, @Valid @RequestBody CreateReceptionistRequest req) {
        try {
            userService.createReceptionist(req.username, req.password, hotelId);
            return ResponseEntity.ok().build();
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            return ResponseEntity.status(409).body("Username already exists");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Database error: " + e.getMessage());
        }
    }

    @PostMapping("/hotels/{hotelId}/managers")
    public ResponseEntity<String> createManager(@PathVariable int hotelId, @Valid @RequestBody CreateManagerRequest req) {
        try {
            userService.createManager(req.username, req.password, hotelId);
            return ResponseEntity.ok().build();
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            return ResponseEntity.status(409).body("Username already exists");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Database error: " + e.getMessage());
        }
    }
}

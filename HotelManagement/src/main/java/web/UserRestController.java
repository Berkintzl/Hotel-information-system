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
    public ResponseEntity<Void> createReceptionist(@PathVariable int hotelId, @Valid @RequestBody CreateReceptionistRequest req) throws SQLException {
        userService.createReceptionist(req.username, req.password, hotelId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hotels/{hotelId}/managers")
    public ResponseEntity<Void> createManager(@PathVariable int hotelId, @Valid @RequestBody CreateManagerRequest req) throws SQLException {
        userService.createManager(req.username, req.password, hotelId);
        return ResponseEntity.ok().build();
    }
}

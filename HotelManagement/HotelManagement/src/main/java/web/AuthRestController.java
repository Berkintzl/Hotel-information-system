package web;

import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AuthenticationService;
import service.HotelService;
import service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthenticationService authenticationService;
    private final HotelService hotelService;
    private final UserService userService;

    public AuthRestController(AuthenticationService authenticationService, HotelService hotelService, UserService userService) {
        this.authenticationService = authenticationService;
        this.hotelService = hotelService;
        this.userService = userService;
    }

    public static class LoginRequest {
        @NotBlank
        public String username;
        @NotBlank
        public String password;
    }

    public static class LoginResponse {
        public int id;
        public String username;
        public String roleName;
        public int hotelId;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) throws SQLException {
        User user = authenticationService.authenticate(request.username, request.password);
        if (user == null) return ResponseEntity.status(401).build();
        LoginResponse resp = new LoginResponse();
        resp.id = user.getId();
        resp.username = user.getUsername();
        resp.roleName = user.getRoleName();
        resp.hotelId = user.getHotelId();
        return ResponseEntity.ok(resp);
    }

    public static class OwnerRegisterRequest {
        @NotBlank
        public String name;
        @NotBlank
        public String address;
        @NotBlank
        public String username;
        @NotBlank
        public String password;
    }

    public static class OwnerRegisterResponse {
        public int userId;
        public int hotelId;
    }

    @PostMapping("/register/owner")
    public ResponseEntity<OwnerRegisterResponse> registerOwner(@Valid @RequestBody OwnerRegisterRequest req) throws SQLException {
        int hotelId = hotelService.createHotel(req.name, req.address);
        userService.createUserWithRole(req.username, req.password, "owner", hotelId);
        OwnerRegisterResponse resp = new OwnerRegisterResponse();
        resp.userId = 0;
        resp.hotelId = hotelId;
        return ResponseEntity.ok(resp);
    }
}

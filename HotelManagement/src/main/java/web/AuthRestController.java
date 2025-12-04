package web;

import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AuthenticationService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthenticationService authenticationService;

    public AuthRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
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
}

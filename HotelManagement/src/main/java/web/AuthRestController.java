package web;

import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AuthenticationService;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthenticationService authenticationService;

    public AuthRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) throws SQLException {
        User user = authenticationService.authenticate(request.username, request.password);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(user);
    }
}

package sandrakorpi.csnfribeloppapi.Controllers;

import sandrakorpi.csnfribeloppapi.Dtos.LoginResponse;
import sandrakorpi.csnfribeloppapi.Dtos.LoginUserDto;
import sandrakorpi.csnfribeloppapi.Dtos.RegisterUserDto;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Security.JwtTokenProvider;
import sandrakorpi.csnfribeloppapi.Services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @Autowired
    public AuthController(
            JwtTokenProvider jwtTokenProvider,
            AuthService authenticationService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterUserDto registerUserDto) {
        try {
            authService.signup(registerUserDto);
            System.out.println("Registreringsdata: " + registerUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (Exception exception) {
            System.out.println("funkade ej Registreringsdata: " + registerUserDto);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/signIn")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginDto) {

        try {
            User authenticatedUser = authService.authenticate(loginDto);

            // Hämta userId direkt från authenticatedUser
            Long userId = authenticatedUser.getId();

            // Skapa en Map för extra claims
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", userId); // Lägg till userId

            // Generera token med extra claims
            String jwtToken = jwtTokenProvider.generateToken(extraClaims, authenticatedUser);

            LoginResponse loginResponse = LoginResponse.builder()
                    .token(jwtToken)
                    .expiresIn(jwtTokenProvider.getExpirationTime())
                    .build();

            return ResponseEntity.ok(loginResponse);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }}}
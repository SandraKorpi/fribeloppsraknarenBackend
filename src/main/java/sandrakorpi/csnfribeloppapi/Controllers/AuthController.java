package sandrakorpi.csnfribeloppapi.Controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sandrakorpi.csnfribeloppapi.Dtos.ChangePasswordDto;
import sandrakorpi.csnfribeloppapi.Dtos.LoginResponse;
import sandrakorpi.csnfribeloppapi.Dtos.LoginUserDto;
import sandrakorpi.csnfribeloppapi.Dtos.RegisterUserDto;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Security.JwtTokenProvider;
import sandrakorpi.csnfribeloppapi.Security.LoginAttemptService;
import sandrakorpi.csnfribeloppapi.Services.AuthService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public AuthController(
            JwtTokenProvider jwtTokenProvider,
            AuthService authenticationService,
            LoginAttemptService loginAttemptService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authenticationService;
        this.loginAttemptService = loginAttemptService;
    }

    // Valideringsfel ger 400 och upptaget användarnamn eller e-post ger 409, båda via GlobalExceptionHandler.
    // Registreringsdata, som innehåller lösenordet, får aldrig skrivas till loggen.
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        authService.signup(registerUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signIn")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginDto) {
        String attemptKey = loginDto.getUserName().trim().toLowerCase(Locale.ROOT);

        if (loginAttemptService.isBlocked(attemptKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        try {
            User authenticatedUser = authService.authenticate(loginDto);
            loginAttemptService.recordSuccess(attemptKey);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", authenticatedUser.getId());
            String jwtToken = jwtTokenProvider.generateToken(extraClaims, authenticatedUser);

            return ResponseEntity.ok(LoginResponse.builder()
                    .token(jwtToken)
                    .expiresIn(jwtTokenProvider.getExpirationTime())
                    .build());
        } catch (AuthenticationException exception) {
            // Samma svar oavsett om användarnamnet saknas eller lösenordet är fel.
            loginAttemptService.recordFailure(attemptKey);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //@AuthenticationPrincipal är en del av Spring Security och hämtar den inloggade användaren.
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto,
                                               @AuthenticationPrincipal User currentUser) {
        authService.changePassword(changePasswordDto, currentUser);
        return ResponseEntity.noContent().build();
    }
}

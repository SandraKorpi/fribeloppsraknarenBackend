package sandrakorpi.csnfribeloppapi.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import sandrakorpi.csnfribeloppapi.Dtos.LoginResponse;
import sandrakorpi.csnfribeloppapi.Dtos.LoginUserDto;
import sandrakorpi.csnfribeloppapi.Dtos.UpdateUserDto;
import sandrakorpi.csnfribeloppapi.Dtos.UserDto;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Security.JwtTokenProvider;
import sandrakorpi.csnfribeloppapi.Services.AuthService;
import sandrakorpi.csnfribeloppapi.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sandrakorpi.csnfribeloppapi.Security.JwtTokenProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    private final AuthService authService;
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, JwtTokenProvider jwtTokenProvider1, AuthService authService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider1;
        this.authService = authService;
    }

    //endast admin ska kunna få utalla users.
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers()
    {
        List<UserDto> userList = userService.findAllUsers();
        return ResponseEntity.ok(userList);
    }

    //endast admin ska kunna radera user.
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteUser (@PathVariable long id)
    {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    //Dessa två metoder ska usern själv kunna komma åt, så de ska hämta id från token och inte anges.
    @PutMapping("/{id}")
    public ResponseEntity<LoginResponse> updateUser(
            @RequestHeader("Authorization") String token, // Token från klienten
            @RequestBody UpdateUserDto updateUserDto, // DTO med de uppdaterade uppgifterna
            @PathVariable Long id) { // ID för användaren som ska uppdateras

        try {
            // 1. Extrahera userId från token
            Long userId = extractUserIdFromToken(token);

            // 2. Kontrollera att användaren endast kan uppdatera sitt eget konto
            if (!userId.equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 3. Anropa UserService för att uppdatera användaren baserat på DTO
            UpdateUserDto updatedUserDto = userService.updateUser(userId, updateUserDto);

            // 4. Skapa nya extra claims för token
            Map<String, Object> extraClaims = Map.of("userId", userId);

            // 5. Generera en ny JWT-token baserat på de uppdaterade uppgifterna
            String newToken = jwtTokenProvider.generateToken(extraClaims, (UserDetails) updatedUserDto);

            // 6. Bygg LoginResponse med den nya tokenen
            LoginResponse loginResponse = LoginResponse.builder()
                    .token(newToken)
                    .expiresIn(jwtTokenProvider.getExpirationTime())
                    .build();

            // 7. Returnera den nya tokenen och uppdaterade data
            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }



    //Metod så admin kan söka efter user med id.
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping({"/{id}"})
    public ResponseEntity<UserDto> getUserById (@PathVariable long id)
    {
        UserDto searchUser = userService.findById(id);
        return ResponseEntity.ok(searchUser);
    }

    //så user kan få sin information utan att ange id.
    @GetMapping({"/getUser"})
    public ResponseEntity<UserDto> getUserforUser (@RequestHeader("Authorization") String token)
    {
        Long userId = extractUserIdFromToken(token);
        UserDto searchUser = userService.findById(userId);
        return ResponseEntity.ok(searchUser);
    }

    // Hjälpfunktion för att extrahera användar-ID från JWT-token
    private Long extractUserIdFromToken(String token) {
        String jwtToken = token.substring(7); // Ta bort "Bearer " från token
        return jwtTokenProvider.extractUserId(jwtToken);
    }
}
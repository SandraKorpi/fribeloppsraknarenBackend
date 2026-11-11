package sandrakorpi.csnfribeloppapi.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import sandrakorpi.csnfribeloppapi.Dtos.LoginResponse;
import sandrakorpi.csnfribeloppapi.Dtos.LoginUserDto;
import sandrakorpi.csnfribeloppapi.Dtos.UpdateUserDto;
import sandrakorpi.csnfribeloppapi.Dtos.UserDto;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Security.JwtTokenProvider;
import sandrakorpi.csnfribeloppapi.Services.AccountService;
import sandrakorpi.csnfribeloppapi.Services.UserService;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountService accountService;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, AccountService accountService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.accountService = accountService;
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
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    //Dessa två metoder ska usern själv kunna komma åt, så de ska hämta id från token och inte anges.
    //Behöver sätta en ny token här om användaren uppdaterar sitt användarnamn då token hänger ihop med namnet.
    @PutMapping("/{id}")
    public ResponseEntity<LoginResponse> updateUser(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdateUserDto updateUserDto,
            @PathVariable Long id) {

        // Extrahera userId från token
        Long userId = extractUserIdFromToken(token);

        // Kontrollera att användaren endast kan uppdatera sitt eget konto
        if (!userId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Uppdatera användaren. Fel, till exempel upptaget användarnamn, hanteras av GlobalExceptionHandler.
        User updatedUser = userService.updateUserEntity(userId, updateUserDto);

        // Skapa extra claims för den nya tokenen
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", updatedUser.getId());

        // Generera ny JWT-token baserad på den uppdaterade användaren
        String newToken = jwtTokenProvider.generateToken(extraClaims, updatedUser);

        return ResponseEntity.ok(LoginResponse.builder()
                .token(newToken)
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .build());
    }

    // Användaren raderar sitt eget konto och all sin data. Krävs av App Store för appar med konton.
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteOwnAccount(@AuthenticationPrincipal User currentUser) {
        accountService.deleteAccount(currentUser.getId());
        return ResponseEntity.noContent().build();
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
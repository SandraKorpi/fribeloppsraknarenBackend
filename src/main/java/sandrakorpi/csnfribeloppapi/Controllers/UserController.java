package sandrakorpi.csnfribeloppapi.Controllers;

import sandrakorpi.csnfribeloppapi.Dtos.UserDto;
import sandrakorpi.csnfribeloppapi.Security.JwtTokenProvider;
import sandrakorpi.csnfribeloppapi.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sandrakorpi.csnfribeloppapi.Security.JwtTokenProvider;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, JwtTokenProvider jwtTokenProvider1) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider1;
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
    @PutMapping({"/{id}"})
    public ResponseEntity<UserDto> updateUser (@RequestHeader("Authorization") String token, @RequestBody UserDto userDto)
    {
        Long userId = extractUserIdFromToken(token);
        UserDto updatedUserDto = userService.updateUser(userId, userDto);
        return ResponseEntity.ok(updatedUserDto);

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
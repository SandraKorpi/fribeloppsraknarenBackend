package sandrakorpi.csnfribeloppapi.Controllers;

import sandrakorpi.csnfribeloppapi.Dtos.UserDto;
import sandrakorpi.csnfribeloppapi.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
//Ska endast komma åt om du är admin, behöver justeras i securityconfig.
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers()
    {
        List<UserDto> userList = userService.findAllUsers();
        return ResponseEntity.ok(userList);
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteUser (@PathVariable long id)
    {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<UserDto> updateUser (@PathVariable long id, @RequestBody UserDto userDto)
    {
        UserDto updatedUserDto = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUserDto);

    }

    @GetMapping({"/{id}"})
    public ResponseEntity<UserDto> getUserById (@PathVariable long id)
    {
        UserDto searchUser = userService.findById(id);
        return ResponseEntity.ok(searchUser);
    }
}
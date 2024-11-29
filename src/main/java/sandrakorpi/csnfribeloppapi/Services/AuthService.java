package sandrakorpi.csnfribeloppapi.Services;

import sandrakorpi.csnfribeloppapi.Dtos.ChangePasswordDto;
import sandrakorpi.csnfribeloppapi.Dtos.LoginUserDto;
import sandrakorpi.csnfribeloppapi.Dtos.RegisterUserDto;
import sandrakorpi.csnfribeloppapi.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(
            UserService userService,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void signup(RegisterUserDto registerUserDto) {
        User user = new User();
        user.setUserName(registerUserDto.getUserName());
        user.setEmail(registerUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword())); // Kryptera lösenordet

        userService.saveUser(user); // Använd User direkt
    }

    public User authenticate(LoginUserDto loginDto) {
        User user = userService.loadUserByUsername(loginDto.getUserName());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUserName(),
                        loginDto.getPassword()
                )
        );

        return user;
    }

    public void changePassword(ChangePasswordDto changePasswordDto, User currentUser) {
        // Kontrollera om det nuvarande lösenordet stämmer
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException("Nuvarande lösenord är felaktigt");

        }

        // Kontrollera om de nya lösenorden matchar
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new IllegalArgumentException("De nya lösenorden matchar inte");
        }

        // Kryptera det nya lösenordet
        currentUser.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));

        // Spara användaren med det nya lösenordet
        userService.updatePassword(currentUser); // Spara användaren med det uppdaterade lösenordet

    }


}



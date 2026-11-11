package sandrakorpi.csnfribeloppapi.Services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import sandrakorpi.csnfribeloppapi.Dtos.UpdateUserDto;
import sandrakorpi.csnfribeloppapi.Dtos.UserDto;
import sandrakorpi.csnfribeloppapi.Enums.Role;
import sandrakorpi.csnfribeloppapi.Exceptions.ResourceNotFoundException;
import sandrakorpi.csnfribeloppapi.Exceptions.UserAlreadyExistsException;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        String email = user.getEmail().trim().toLowerCase(Locale.ROOT);
        user.setEmail(email);

        if (userRepository.existsByUserName(user.getUsername())) {
            throw new UserAlreadyExistsException("Användarnamnet är redan taget");
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("E-postadressen är redan registrerad");
        }

        // Alla nya konton är vanliga användare. Admin ges separat i databasen, aldrig via registrering.
        user.addRole(Role.ROLE_USER);

        return userRepository.save(user);
    }
    public void updatePassword(User user) {
        // Uppdatera bara lösenordet för användaren
        userRepository.save(user); // Endast lösenordet uppdateras här
    }

    public List<UserDto> findAllUsers() {
        List<User> userList = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();

        // Konvertera varje User till UserDto
        for (User user : userList) {
            userDtoList.add(convertToUserDto(user));
        }
        return userDtoList;
    }

    public User updateUserEntity(Long userId, UpdateUserDto updateDto) {
        User userToUpdate = getUserEntityById(userId);

        // Validera endast om användarnamnet har ändrats och tillhör en annan användare
        if (updateDto.getUserName() != null
                && !updateDto.getUserName().equals(userToUpdate.getUsername())
                && userRepository.existsByUserName(updateDto.getUserName())) {
            throw new UserAlreadyExistsException("Användarnamnet är redan taget.");
        }

        // Validera endast om e-postadressen har ändrats och tillhör en annan användare
        String newEmail = updateDto.getEmail() == null ? null : updateDto.getEmail().trim().toLowerCase(Locale.ROOT);
        if (newEmail != null
                && !newEmail.equals(userToUpdate.getEmail())
                && userRepository.existsByEmail(newEmail)) {
            throw new UserAlreadyExistsException("E-postadressen är redan registrerad.");
        }

        // Uppdatera endast om det finns nya värden
        if (updateDto.getUserName() != null) {
            userToUpdate.setUserName(updateDto.getUserName());
        }
        if (newEmail != null) {
            userToUpdate.setEmail(newEmail);
        }

        return userRepository.save(userToUpdate);
    }



    //metod som konverterar user till updateuserdto
    public UpdateUserDto convertToUpdateUserDto(User user)
    {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setUserName(user.getUsername());
        return updateUserDto;
    }


    public void deleteUser(long id) {
        User userToDelete = getUserEntityById(id);
        userRepository.delete(userToDelete);
    }

    // Metod för att hämta en användare baserat på ID
    public User getUserEntityById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Användare med ID " + id + " hittades inte"));
    }

    // Metod för att konvertera UserDto till User
    public User convertToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        // Lösenordet hanteras inte i UserDto
        user.setRoles(userDto.getRoles());
        return user;
    }

    // Metod för att konvertera User till UserDto
    public UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUserName(user.getUsername());
        userDto.setEmail(user.getEmail());
        // Lösenordet sätts inte i DTO:n
        userDto.setRoles(user.getRoles());
        return userDto;
    }

    // Metod för att hämta användare med ID och returnera som DTO
    public UserDto findById(long id) {
        User user = getUserEntityById(id);
        return convertToUserDto(user);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Användaren hittades inte");
        }
        return user;
    }
}


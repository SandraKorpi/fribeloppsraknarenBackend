package sandrakorpi.csnfribeloppapi.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sandrakorpi.csnfribeloppapi.Dtos.UpdateUserDto;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User mockUser;
    private UpdateUserDto updateDtoMock;

    @BeforeEach
    void setUp() {
      mockUser = new User();
        mockUser.setUserName("sandra");
        mockUser.setEmail("saandra92@hotmail.com");
        mockUser.setId(1L);
        mockUser.setPassword("password");

        updateDtoMock = new UpdateUserDto();
        updateDtoMock.setUserName("Ronja");
        updateDtoMock.setEmail("ronja@hotmail.com");
    }
  /*  @Test
    void saveUser() {


    }

    @Test
    void updatePassword() {
    }

    @Test
    void findAllUsers() {
    }

 */

    @Test
    void updateUserEntity() {
        //metoden hittar användaren, ger nya uppgifter och sedan sparar dem.
        //därför måste även save användas.
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        User newUpdateDto = userService.updateUserEntity(1L, updateDtoMock);
        assertEquals(newUpdateDto.getUsername(), "Ronja");
        assertEquals(newUpdateDto.getEmail(), "ronja@hotmail.com");
    }
/*
    @Test
    void convertToUpdateUserDto() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void getUserEntityById() {
    }

    @Test
    void convertToUser() {
    }

    @Test
    void convertToUserDto() {
    }

    @Test
    void findById() {
    }

    @Test
    void loadUserByUsername() {
    }

 */
}


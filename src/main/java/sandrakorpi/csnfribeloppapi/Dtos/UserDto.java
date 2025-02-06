package sandrakorpi.csnfribeloppapi.Dtos;

import lombok.*;
import sandrakorpi.csnfribeloppapi.Enums.Role;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;
    private String userName;
    private String email;
  //ska ej returnera nått lösenord  private String password;
    private List<Role> roles;
}
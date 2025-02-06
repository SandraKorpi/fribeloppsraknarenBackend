package sandrakorpi.csnfribeloppapi.Dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterUserDto {
    private String userName;
    private String email;
    private String password;
}

package sandrakorpi.csnfribeloppapi.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterUserDto {

    @NotBlank(message = "Ange ett användarnamn")
    @Size(min = 3, max = 50, message = "Användarnamnet ska vara 3 till 50 tecken")
    @Pattern(regexp = "^[\\p{L}0-9._-]+$", message = "Användarnamnet får bara innehålla bokstäver, siffror, punkt, understreck och bindestreck")
    private String userName;

    @NotBlank(message = "Ange en e-postadress")
    @Email(message = "Ogiltig e-postadress")
    @Size(max = 254, message = "E-postadressen är för lång")
    private String email;

    // BCrypt läser bara de första 72 byten, därför är 72 en rimlig övre gräns.
    @ToString.Exclude
    @NotBlank(message = "Ange ett lösenord")
    @Size(min = 10, max = 72, message = "Lösenordet ska vara 10 till 72 tecken")
    private String password;
}

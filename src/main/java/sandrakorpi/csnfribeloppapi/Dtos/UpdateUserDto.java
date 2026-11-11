package sandrakorpi.csnfribeloppapi.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {

    // Null betyder att fältet inte ändras.
    @Size(min = 3, max = 50, message = "Användarnamnet ska vara 3 till 50 tecken")
    private String userName;

    @Email(message = "Ogiltig e-postadress")
    @Size(max = 254, message = "E-postadressen är för lång")
    private String email;

}

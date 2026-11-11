package sandrakorpi.csnfribeloppapi.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserDto {

    @NotBlank
    @Size(max = 50)
    private String userName;

    @ToString.Exclude
    @NotBlank
    @Size(max = 72)
    private String password;
}

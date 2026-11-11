package sandrakorpi.csnfribeloppapi.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {

    @NotBlank(message = "Ange nuvarande lösenord")
    @Size(max = 72)
    private String currentPassword;

    @NotBlank(message = "Ange ett nytt lösenord")
    @Size(min = 10, max = 72, message = "Lösenordet ska vara 10 till 72 tecken")
    private String newPassword;

    @NotBlank(message = "Bekräfta det nya lösenordet")
    @Size(max = 72)
    private String confirmPassword;
}

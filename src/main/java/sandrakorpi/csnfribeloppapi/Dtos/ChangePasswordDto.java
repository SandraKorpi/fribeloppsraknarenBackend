package sandrakorpi.csnfribeloppapi.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

}

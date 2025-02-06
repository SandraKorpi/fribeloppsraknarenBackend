package sandrakorpi.csnfribeloppapi.Dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
@Getter
@Setter
public class LoginResponse {

    private String token;

    private long expiresIn;

}

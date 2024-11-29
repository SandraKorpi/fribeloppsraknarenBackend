package sandrakorpi.csnfribeloppapi.Dtos;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    private String userName;
    private String email;

}
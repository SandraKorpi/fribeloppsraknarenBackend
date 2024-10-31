package sandrakorpi.csnfribeloppapi.Dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WorkedHoursDto {
    private double hours; // Arbetade timmar
    private int month; // Månad (1-12)
    private int date; // datum för arbete
    private int year; // År
    private Long userId; // ID för kopplad användare
}
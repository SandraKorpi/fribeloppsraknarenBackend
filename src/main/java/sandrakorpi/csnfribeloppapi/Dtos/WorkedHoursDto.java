package sandrakorpi.csnfribeloppapi.Dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.DateTimeException;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WorkedHoursDto {

    @DecimalMin(value = "0", message = "Timmar kan inte vara negativa")
    @DecimalMax(value = "24", message = "Ett arbetspass kan inte vara längre än 24 timmar")
    private double hours; // Arbetade timmar

    @Min(value = 1, message = "Månad måste vara 1 till 12")
    @Max(value = 12, message = "Månad måste vara 1 till 12")
    private int month; // Månad (1-12)

    @Min(value = 1, message = "Datum måste vara 1 till 31")
    @Max(value = 31, message = "Datum måste vara 1 till 31")
    private int date; // datum för arbete

    @Min(value = 2000, message = "Ogiltigt år")
    @Max(value = 2100, message = "Ogiltigt år")
    private int year; // År

    @DecimalMin(value = "0", message = "Timlönen kan inte vara negativ")
    @DecimalMax(value = "100000", message = "Timlönen är orimligt hög")
    private double hourlyRate; //timlön

    @DecimalMin(value = "0", message = "Semesterersättningen kan inte vara negativ")
    @DecimalMax(value = "100", message = "Semesterersättningen är i procent och kan inte vara över 100")
    private double vacationPay; //semesterersättning i procent

    // Skrivs aldrig av klienten. Controllern sätter alltid användaren utifrån inloggningen.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId; // ID för kopplad användare

    @JsonIgnore
    @AssertTrue(message = "Datumet finns inte i kalendern")
    public boolean isValidDate() {
        try {
            LocalDate.of(year, month, date);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}

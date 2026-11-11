package sandrakorpi.csnfribeloppapi.Dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyPeriodDto {

    /** Sätts av servern från URL:en. */
    private Integer year;
    private SemesterType type;

    @Min(value = 1, message = "Antal veckor måste vara minst 1")
    @Max(value = 26, message = "Antal veckor får vara högst 26")
    private int weeks;

    @Min(value = 50, message = "Studietakten måste vara 100, 75 eller 50 procent")
    @Max(value = 100, message = "Studietakten måste vara 100, 75 eller 50 procent")
    private int studyRate;

    /** Valfritt: fribeloppet enligt ditt CSN-beslut, om du vill använda det i stället för tabellen. */
    @DecimalMin(value = "0", message = "Fribeloppet kan inte vara negativt")
    @DecimalMax(value = "1000000", message = "Fribeloppet är orimligt högt")
    private Double fribeloppOverride;
}

package sandrakorpi.csnfribeloppapi.Dtos;

import lombok.*;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SemesterDto {
        private Long id;
        private String name; // "Vår" eller "Höst"
        private double allowance; // Fribelopp
        private List<Integer> months; // Månad (1-12)
        private SemesterType type; // Typ av termin (HT eller VT)
}

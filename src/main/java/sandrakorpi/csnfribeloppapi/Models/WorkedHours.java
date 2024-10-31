package sandrakorpi.csnfribeloppapi.Models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkedHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double hours; // Arbetade timmar
    private int month; // Månad (1-12)
    private int date; //Datum för arbete
    private int year; // År


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}

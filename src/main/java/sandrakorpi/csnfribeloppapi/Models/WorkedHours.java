package sandrakorpi.csnfribeloppapi.Models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkedHours {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double hours; // Arbetade timmar
    private int month; // Månad (1-12)
    private int year; // År

    @ManyToOne
    @JoinColumn(name = "user_id") // Foreign key till User
    private User user; // Användare som registrerat timmarna

}

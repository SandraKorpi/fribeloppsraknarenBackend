package sandrakorpi.csnfribeloppapi.Models;

import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Semester {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Namnet på semestern
    private double incomeLimit; // Fribelopp
    @Enumerated(EnumType.STRING)
    private SemesterType type; // HT eller VT
    @OneToMany(mappedBy = "semester")
    private List<User> users; // Koppling till användare

}
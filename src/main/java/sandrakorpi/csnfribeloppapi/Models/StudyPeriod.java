package sandrakorpi.csnfribeloppapi.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;

/**
 * Hur mycket användaren studerar med studiemedel under ett kalenderhalvår.
 * Fribeloppet beror på antal veckor och studietakt, och är därför personligt,
 * till skillnad från den gamla gemensamma Semester.incomeLimit.
 */
@Entity
@Table(
        name = "study_period",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "year", "type"})
)
@Getter
@Setter
@NoArgsConstructor
public class StudyPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int year;

    /** VT = januari till juni, HT = juli till december. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SemesterType type;

    /** Antal veckor med studiemedel under halvåret, 1-26. */
    @Column(nullable = false)
    private int weeks;

    /** Studietakt i procent: 100, 75 eller 50. */
    @Column(nullable = false)
    private int studyRate;

    /** Fribelopp enligt användarens CSN-beslut. Används före tabellvärdet om det är satt. */
    private Double fribeloppOverride;
}

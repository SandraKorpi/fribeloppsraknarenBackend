package sandrakorpi.csnfribeloppapi.Dtos;

import lombok.Builder;
import lombok.Data;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;

/**
 * Var användaren står i förhållande till fribeloppet för ett kalenderhalvår.
 * Är configured false saknas uppgift om antal veckor och studietakt, och bara income är satt.
 */
@Data
@Builder
public class FribeloppStatusDto {

    public static final String SOURCE_OWN_VALUE = "OWN_VALUE";
    public static final String SOURCE_CSN_TABLE = "CSN_TABLE";
    public static final String SOURCE_ESTIMATED = "ESTIMATED";

    private int year;
    private SemesterType type;
    private boolean configured;

    private Integer weeks;
    private Integer studyRate;

    private Long fribelopp;
    /** OWN_VALUE, CSN_TABLE (rakt ur CSN:s tabell) eller ESTIMATED (skalad med prisbasbeloppet). */
    private String fribeloppSource;

    private long income;
    private Long remaining;
    private Long over;
    private Long estimatedReduction;
    private boolean exceeded;

    private Double averageHourlyPay;
    private Double extraHours;
}

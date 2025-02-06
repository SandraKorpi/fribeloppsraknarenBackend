package sandrakorpi.csnfribeloppapi.Models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@AllArgsConstructor // Lombok skapar en konstruktor för alla fält
@NoArgsConstructor
public class WorkedHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double hours; // Arbetade timmar
    private int month; // Månad (1-12)
    private int date; //Datum för arbete
    private int year; // År
    private double hourlyRate; //timlön för timmarna
    private double vacationPay; // semesterersättning i procent

    public WorkedHours(double hours, int month, int date, int year, double hourlyRate, double vacationPay) {
        this.hours = hours;
        this.month = month;
        this.date = date;
        this.year = year;
        this.hourlyRate = hourlyRate;
        this.vacationPay = vacationPay;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "semester_id", referencedColumnName = "id") // Ny FK till Semester
    private Semester semester; // Koppling till termin


    public Long getUserId(){
    return user.getId();
}

}

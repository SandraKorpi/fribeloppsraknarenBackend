package sandrakorpi.csnfribeloppapi.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Exceptions.ResourceNotFoundException;
import sandrakorpi.csnfribeloppapi.Models.Semester;
import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import sandrakorpi.csnfribeloppapi.Repositories.SemesterRepository;
import sandrakorpi.csnfribeloppapi.Repositories.WorkedHoursRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalculationService {

    private final SemesterService semesterService;
    private final WorkedHoursRepository workedHoursRepository;
    private final SemesterRepository semesterRepository;

    @Autowired
    public CalculationService(SemesterService semesterService, WorkedHoursRepository workedHoursRepository, SemesterRepository semesterRepository) {
        this.semesterService = semesterService;
        this.workedHoursRepository = workedHoursRepository;
        this.semesterRepository = semesterRepository;
    }

    @Transactional
    public double calculateSemesterIncome(long userId, int year, SemesterType semesterType) {
        List<Integer> months = semesterService.getMonthsForSemesterType(semesterType);
        List<WorkedHours> workedHoursList = new ArrayList<>();
        for (Integer m : months) {
            workedHoursList.addAll(workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, m));
        }
        return calculateIncome(workedHoursList);
    }
    public double calculateSemesterIncomeWithVacationPay(long userId, int year, SemesterType semesterType) {
        List<Integer> months = semesterService.getMonthsForSemesterType(semesterType);
        List<WorkedHours> workedHoursList = new ArrayList<>();

        // Hämta alla arbetstimmar för den terminen
        for (Integer month : months) {
            workedHoursList.addAll(workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, month));
        }

        // Beräkna total inkomst inklusive semesterersättning
        double totalIncome = 0.0;
        for (WorkedHours workedHours : workedHoursList) {
            double income = workedHours.getHours() * workedHours.getHourlyRate();
            double vacationPay = income * (workedHours.getVacationPay() / 100);
            totalIncome += income + vacationPay;
        }

        return totalIncome;
    }
    public String compareSemesterIncome(long userId, int year, SemesterType semesterType) {
        // Hämta fribelopp för terminen
        Semester semester = semesterRepository.findByTypeAndYear(semesterType, year);
        double incomeLimit = semester.getIncomeLimit();

        // Beräkna total inkomst för terminen inklusive semesterersättning
        double totalIncome = calculateSemesterIncomeWithVacationPay(userId, year, semesterType);
        double difference = incomeLimit - totalIncome;

        if (difference <= 0) {
            return "Du har nått eller överskridit fribeloppet för denna termin.";
        }

        // Beräkna medeltimlön
        double averageHourlyRate = calculateAverageHourlyRate(userId, year, semesterType);
        double additionalHours = difference / averageHourlyRate;

        return String.format(
                "Du kan tjäna %.2f kr mer denna termin utan att överskrida fribeloppet. Det motsvarar %.2f extra timmar, baserat på din medelinkomst/timme",
                difference, additionalHours
        );
    }
    public double calculateAverageHourlyRate(long userId, int year, SemesterType semesterType) {
        List<Integer> months = semesterService.getMonthsForSemesterType(semesterType);
        List<WorkedHours> workedHoursList = new ArrayList<>();

        // Hämta alla arbetstimmar för den terminen
        for (Integer month : months) {
            workedHoursList.addAll(workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, month));
        }

        // Beräkna medeltimlön
        double totalHourlyRate = 0.0;
        int count = 0;
        for (WorkedHours workedHours : workedHoursList) {
            totalHourlyRate += workedHours.getHourlyRate();
            count++;
        }

        return count > 0 ? totalHourlyRate / count : 0.0;
    }
    @Transactional
    public double calculateYearlyIncome(long userId, int year) {
        List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndYear(userId, year);
        return calculateIncome(workedHoursList);
    }

    @Transactional
    public double calculateShiftIncome (long userId, long workedHoursId)
    {
        WorkedHours workedHours = workedHoursRepository.findByIdAndUser_Id(workedHoursId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Inga arbetade timmar hittades med id: " + workedHoursId));
        return workedHours.getHours() * workedHours.getHourlyRate();
    }
    @Transactional
    public double calculateMonthlyIncome(long userId, int year, int month) {
        List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, month);
        return calculateIncome(workedHoursList);
    }
    private double calculateIncome(List<WorkedHours> workedHoursList) {
        double totalIncome = 0.0;
        for (WorkedHours workedHours : workedHoursList) {
            double hoursIncome = workedHours.getHours() * workedHours.getHourlyRate();
            double vacationPay = (workedHours.getVacationPay() / 100) * hoursIncome; // Beräkna semesterersättning baserat på procenten
            totalIncome += hoursIncome + vacationPay; // Lägg till både timmar och semesterersättning
        }
        return totalIncome;
    }
}
package sandrakorpi.csnfribeloppapi.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sandrakorpi.csnfribeloppapi.Dtos.UserDto;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Exceptions.ResourceNotFoundException;
import sandrakorpi.csnfribeloppapi.Models.Semester;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import sandrakorpi.csnfribeloppapi.Dtos.FribeloppStatusDto;
import sandrakorpi.csnfribeloppapi.Fribelopp.FribeloppCalculator;
import sandrakorpi.csnfribeloppapi.Repositories.WorkedHoursRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CalculationService {

    private final SemesterService semesterService;
    private final WorkedHoursRepository workedHoursRepository;
    private final StudyPeriodService studyPeriodService;

    @Autowired
    public CalculationService(SemesterService semesterService, WorkedHoursRepository workedHoursRepository, StudyPeriodService studyPeriodService) {
        this.semesterService = semesterService;
        this.workedHoursRepository = workedHoursRepository;
        this.studyPeriodService = studyPeriodService;
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

        return Math.round(totalIncome);
    }
    /**
     * Jämför användarens inkomst under halvåret med hens personliga fribelopp, som beror på
     * antal studieveckor och studietakt. Saknas de uppgifterna returneras bara inkomsten.
     */
    public FribeloppStatusDto getFribeloppStatus(long userId, int year, SemesterType semesterType) {
        long income = Math.round(calculateSemesterIncomeWithVacationPay(userId, year, semesterType));

        Optional<StudyPeriodService.ResolvedFribelopp> resolved = studyPeriodService.resolve(userId, year, semesterType);
        if (resolved.isEmpty()) {
            return FribeloppStatusDto.builder()
                    .year(year)
                    .type(semesterType)
                    .configured(false)
                    .income(income)
                    .build();
        }

        StudyPeriodService.ResolvedFribelopp fribelopp = resolved.get();
        FribeloppCalculator.Result result = FribeloppCalculator.evaluate(fribelopp.amount(), income);
        double averageHourlyPay = calculateAverageHourlyRate(userId, year, semesterType);

        return FribeloppStatusDto.builder()
                .year(year)
                .type(semesterType)
                .configured(true)
                .weeks(fribelopp.weeks())
                .studyRate(fribelopp.studyRate())
                .fribelopp(result.fribelopp())
                .fribeloppSource(fribelopp.source())
                .income(result.income())
                .remaining(result.remaining())
                .over(result.over())
                .estimatedReduction(result.estimatedReduction())
                .exceeded(result.exceeded())
                .averageHourlyPay(Math.round(averageHourlyPay * 100.0) / 100.0)
                .extraHours(FribeloppCalculator.extraHours(result.remaining(), averageHourlyPay))
                .build();
    }

   /* public double calculateAverageHourlyRate(long userId, int year, SemesterType semesterType) {
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
    } */
   public double calculateAverageHourlyRate(long userId, int year, SemesterType semesterType) {
       List<Integer> months = semesterService.getMonthsForSemesterType(semesterType);
       List<WorkedHours> workedHoursList = new ArrayList<>();

       // Hämta alla arbetstimmar för den terminen
       for (Integer month : months) {
           workedHoursList.addAll(workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, month));
       }

       // Beräkna den totala lönen och de totala arbetade timmarna
       double totalIncome = 0.0;
       double totalHours = 0.0;

       for (WorkedHours workedHours : workedHoursList) {
           double hourlyRateWithVacation = workedHours.getHourlyRate() * (1 + workedHours.getVacationPay() / 100.0); // Ta hänsyn till semesterersättning
           totalIncome += workedHours.getHours() * hourlyRateWithVacation; // Lön för arbetade timmar inklusive semesterersättning
           totalHours += workedHours.getHours(); // Totalt antal arbetade timmar
       }

       // Beräkna snitttimlön
       return totalHours > 0 ? totalIncome / totalHours : 0.0;
   }

    @Transactional
    public double calculateYearlyIncome(long userId, int year) {
        List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndYear(userId, year);
        return calculateIncome(workedHoursList);
    }

    //behöver inkludera semesterersättning i test och metod
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
            double vacationPay = (workedHours.getVacationPay() / 100) * hoursIncome;
            totalIncome += hoursIncome + vacationPay;
        }
        return totalIncome;
    }
}
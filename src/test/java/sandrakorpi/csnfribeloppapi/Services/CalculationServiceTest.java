package sandrakorpi.csnfribeloppapi.Services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import sandrakorpi.csnfribeloppapi.Repositories.SemesterRepository;
import sandrakorpi.csnfribeloppapi.Repositories.WorkedHoursRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CalculationServiceTest {
    @Mock
    private SemesterService semesterService;

    @Mock
    private WorkedHoursRepository workedHoursRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @InjectMocks
    private CalculationService calculationService;
    private long userId;
    private SemesterType semesterType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        semesterType = SemesterType.VT;


    }
    @AfterEach
    void tearDown() {
    }


    @Test
    void calculateSemesterIncomeWithVacationPay(){
        WorkedHours workedHours1 = new WorkedHours(10.0, 1, 1, 2024, 100.0, 12.0);
        WorkedHours workedHours2 = new WorkedHours(3.0, 2, 12, 2024, 150.0, 10.0);
        List<WorkedHours> workedHoursList = new ArrayList<>();
        workedHoursList.add(workedHours1);  // Lägg till första objektet
        workedHoursList.add(workedHours2);  // Lägg till andra objektet
    // Mocka beteende för semesterService och workedHoursRepository
    when(semesterService.getMonthsForSemesterType(semesterType)).thenReturn(Arrays.asList(1, 2)); // Våren: januari, februari
    when(workedHoursRepository.findByUser_IdAndYearAndMonth(userId, 2024, 1)).thenReturn(workedHoursList);

    // Anropa metoden
    double result = calculationService.calculateSemesterIncomeWithVacationPay(userId, 2024, semesterType);

    // Beräknat resultat baserat på testdata
    double expected = (10 * 100 * 1.12) + (3 * 150 * 1.10);
    // Kontrollera resultatet
    assertEquals(expected, result, 0.01); // Tillåter en liten avvikelse (0.01) för decimalprecision
}

   /* @Test
    void compareSemesterIncome() {
    }

    @Test
    void calculateAverageHourlyRate() {
    }

    @Test
    void calculateYearlyIncome() {
    }

    @Test
    void calculateShiftIncome() {
    }

    @Test
    void calculateMonthlyIncome() {
    } */
}
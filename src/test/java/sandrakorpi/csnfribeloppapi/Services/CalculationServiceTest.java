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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

@Test
    void compareSemesterIncomeWithSemesterPay() {
        //anger användaren, år och vilken temin som ska jämföras med.
    long userId = 1L;
    int year = 2024;
    SemesterType semesterType = SemesterType.VT;
    WorkedHours workedHours1 = new WorkedHours();
    workedHours1.setHours(40);
    workedHours1.setHourlyRate(200);
    workedHours1.setVacationPay(12);
    // 40 timmar, 200 SEK/timme, 12% semesterersättning
    WorkedHours workedHours2 = new WorkedHours();
    workedHours2.setHours(20);
    workedHours2.setHourlyRate(150);
    workedHours2.setVacationPay(10);
    // 20 timmar, 150 SEK/timme, 10% semesterersättning

    List<Integer> months = Arrays.asList(1, 2, 3); // Exempel på månader
    when(semesterService.getMonthsForSemesterType(semesterType)).thenReturn(months);

    when(workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, 1)).thenReturn(Arrays.asList(workedHours1));
    when(workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, 2)).thenReturn(Arrays.asList(workedHours2));
    when(workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, 3)).thenReturn(Arrays.asList());

    double result = calculationService.calculateSemesterIncomeWithVacationPay(userId, year, semesterType);
    // Assert
    double expectedIncome = Math.round(
            (40 * 200 + (40 * 200 * 0.12)) +
                    (20 * 150 + (20 * 150 * 0.10))
    );
    assertEquals(expectedIncome, result);
    verify(semesterService).getMonthsForSemesterType(semesterType);
    verify(workedHoursRepository, times(3)).findByUser_IdAndYearAndMonth(eq(userId), eq(year), anyInt());
    }


    @Test
    void calculateAverageHourlyRate() {
        //anger användaren, år och vilken temin som ska jämföras med.
        long userId = 1L;
        int year = 2024;
        SemesterType semesterType = SemesterType.VT;
        WorkedHours workedHours1 = new WorkedHours();
        workedHours1.setHours(40);
        workedHours1.setHourlyRate(200);
        workedHours1.setVacationPay(12);
        // 40 timmar, 200 SEK/timme, 12% semesterersättning
        WorkedHours workedHours2 = new WorkedHours();
        workedHours2.setHours(20);
        workedHours2.setHourlyRate(150);
        workedHours2.setVacationPay(10);
        // 20 timmar, 150 SEK/timme, 10% semesterersättning

        List<Integer> months = Arrays.asList(1, 2, 3); // Exempel på månader
        when(semesterService.getMonthsForSemesterType(semesterType)).thenReturn(months);

        when(workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, 1)).thenReturn(Arrays.asList(workedHours1));
        when(workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, 2)).thenReturn(Arrays.asList(workedHours2));
        when(workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, 3)).thenReturn(Arrays.asList());

        double result = calculationService.calculateAverageHourlyRate(userId, year, semesterType);
        assertEquals(result, 204.33, 0.01);
        verify(semesterService).getMonthsForSemesterType(semesterType);
        verify(workedHoursRepository, times(3)).findByUser_IdAndYearAndMonth(eq(userId), eq(year), anyInt());
    }

    /*

    @Test
    void calculateYearlyIncome() {
    }

       @Transactional
    public double calculateShiftIncome (long userId, long workedHoursId)
    {
        WorkedHours workedHours = workedHoursRepository.findByIdAndUser_Id(workedHoursId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Inga arbetade timmar hittades med id: " + workedHoursId));
        return Math.round(workedHours.getHours() * workedHours.getHourlyRate());
*/

    //behöver inkludera semesterersättning i test och metod
    @Test
    void calculateShiftIncome() {
        //anger användaren, år och vilken temin som ska jämföras med.
        long userId = 1L;
        WorkedHours workedHours1 = new WorkedHours();
        workedHours1.setId(1L);
        workedHours1.setHours(40);
        workedHours1.setHourlyRate(200);
       // workedHours1.setVacationPay(12);
        // 40 timmar, 200 SEK/timme, 12% semesterersättning
        long userId2 = 2L;
        WorkedHours workedHours2 = new WorkedHours();
        workedHours2.setId(2L);
        workedHours2.setHours(20);
        workedHours2.setHourlyRate(150);
       // workedHours2.setVacationPay(10);
        // 20 timmar, 150 SEK/timme, 10% semesterersättning
        when(workedHoursRepository.findByIdAndUser_Id(userId, workedHours1.getId())).thenReturn(Optional.of(workedHours1));
        when(workedHoursRepository.findByIdAndUser_Id(userId2, workedHours2.getId())).thenReturn(Optional.of(workedHours2));
       double result = calculationService.calculateShiftIncome(userId, workedHours1.getId());
        double result2 = calculationService.calculateShiftIncome(userId2, workedHours2.getId());

        assertEquals(3000, result2, 0.01);
        assertEquals(result, 8000, 0.01);
        verify(workedHoursRepository, times(2)).findByIdAndUser_Id(anyLong(), anyLong());

    }
/*
    @Test
    void calculateMonthlyIncome() {
    } */
}
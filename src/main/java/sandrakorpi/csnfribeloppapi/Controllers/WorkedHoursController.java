package sandrakorpi.csnfribeloppapi.Controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sandrakorpi.csnfribeloppapi.Dtos.WorkedHoursDto;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import sandrakorpi.csnfribeloppapi.Security.JwtTokenProvider;
import sandrakorpi.csnfribeloppapi.Services.CalculationService;
import sandrakorpi.csnfribeloppapi.Services.SemesterService;
import sandrakorpi.csnfribeloppapi.Services.UserService;
import sandrakorpi.csnfribeloppapi.Services.WorkedHoursService;

import java.util.List;

@RestController
@RequestMapping("/api/worked-hours")
public class WorkedHoursController {
    private final WorkedHoursService workedHoursService;
    private final CalculationService calculationService;
    private final JwtTokenProvider jwtTokenProvider;

    public WorkedHoursController(WorkedHoursService workedHoursService, CalculationService calculationService, JwtTokenProvider jwtTokenProvider) {
        this.workedHoursService = workedHoursService;
        this.calculationService = calculationService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Ger en lista på alla arbetade timmar. Kanske inte något en användare vill ha. Passande för admin eller inte alls?
    @GetMapping("/allTotal")
    private ResponseEntity<List<WorkedHoursDto>> getAllHours(@Parameter(description = "Bearer token", required = true) @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        List<WorkedHoursDto> workedHoursDtoList = workedHoursService.getWorkedHoursForUser(userId);
        return ResponseEntity.ok(workedHoursDtoList);
    }

    // Ger timmar arbetade per termin!
    @GetMapping("/totalForSemester/year/{year}/semestertype/{semesterType}/")
    public ResponseEntity<Double> getHoursBySemester(@PathVariable SemesterType semesterType, int year, @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        double total = workedHoursService.getWorkedHoursForYearSemester(userId, year, semesterType);
        return ResponseEntity.ok(total);
    }

    // Ger totala inkomsten för den månad som användaren söker på.
    @GetMapping("/year/{year}/month/{month}")
    public ResponseEntity<Double> getHoursByYearMonth(@PathVariable int year, @PathVariable int month, @Parameter(description = "Bearer token", required = true) @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        double total = workedHoursService.getWorkedHoursByYearMonth(userId, year, month);
        return ResponseEntity.ok(total);
    }

    // Hämtar specifika timmar utifrån datum.
   @GetMapping("/getByDate/year/{year}/month/{month}/date/{date}")
    public ResponseEntity<Double> getHoursByYearMonthAndDate(@PathVariable int year, @PathVariable int month, @PathVariable int date, @Parameter(description = "Bearer token", required = true) @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        double total = workedHoursService.getWorkedHoursByYearMonthDate(userId, year, month, date);
        return ResponseEntity.ok(total);
    }

    // Hämtar totala timmarna för en termin.
    @GetMapping("/year/{year}")
    public ResponseEntity<Double> getHoursByYear(@PathVariable int year, @Parameter(description = "Bearer token", required = true) @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        double totalHours = workedHoursService.getWorkedHoursByYear(year, userId);
        return ResponseEntity.ok(totalHours);
    }

    // Registrerar timmar för användaren.
    @PostMapping("/addHours")
    public ResponseEntity<WorkedHoursDto> addWorkedHours(@RequestBody WorkedHoursDto workedHoursDto) {
        WorkedHoursDto savedHours = workedHoursService.saveWorkedHours(workedHoursDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHours);
    }

    // Tar bort specifikt arbetspass via id, vet ej hur användbar metod.. Kanske admin?
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id, @Parameter(description = "Bearer token", required = true) @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        workedHoursService.deleteHours(userId, id);
        return ResponseEntity.noContent().build();
    }

    // Tar bort timmarna för ett specifikt datum i vald månad.
    @DeleteMapping("/delete/year/{year}/month/{month}/date/{date}")
    public ResponseEntity<Void> deleteByDate(@PathVariable int year, @PathVariable int month, @PathVariable int date, @Parameter(description = "Bearer token", required = true) @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        workedHoursService.deleteHoursByMonthDate(userId, year, month, date);
        return ResponseEntity.noContent().build();
    }

    // Tar bort alla timmarna i vald månad.
    @DeleteMapping("/year/{year}/month/{month}")
    public ResponseEntity<Void> deleteByMonth(@PathVariable int year, @PathVariable int month, @Parameter(description = "Bearer token", required = true) @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        workedHoursService.deleteHoursByYearMonth(userId, year, month);
        return ResponseEntity.noContent().build();
    }

    // Raderar timmar för ett helt år. Är det användbart?
    @DeleteMapping("/year/{year}")
    public ResponseEntity<Void> deleteByYear(@PathVariable int year, @Parameter(description = "Bearer token", required = true) @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        workedHoursService.deleteByYear(userId, year);
        return ResponseEntity.noContent().build();
    }

    // uppdatering söker på userid för att hitta användarens timme på rätt datum
    @PutMapping("/year/{year}/month/{month}/date/{date}")
    public ResponseEntity<WorkedHoursDto> updateWorkedHoursByUserId(
            @Parameter(description = "Bearer token", required = true) @RequestHeader("Authorization") String token,
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int date,
            @RequestBody WorkedHoursDto workedHoursDto) {

        Long extractedUserId = extractUserIdFromToken(token);
        WorkedHoursDto updatedWorkedHours = workedHoursService.updateWorkedHoursByUserId(extractedUserId,year, month, date, workedHoursDto);
        return ResponseEntity.ok(updatedWorkedHours);
    }


    // Räknar ut hur mycket en användare tjänat en månad.
    @GetMapping("/monthly-income/{year}/{month}")
    public ResponseEntity<Double> calculateMonthlyIncome(@RequestHeader("Authorization") String token,
                                                         @PathVariable int year,
                                                         @PathVariable int month) {
        Long userId = extractUserIdFromToken(token);
        double income = calculationService.calculateMonthlyIncome(userId, year, month);
        return ResponseEntity.ok(income);
    }



    // Räknar ut vad användaren tjänat en termin.
    @GetMapping("/semester-income/{year}/{semester}")
    public ResponseEntity<Double> calculateSemesterIncome(@RequestHeader("Authorization") String token,
                                                          @PathVariable int year,
                                                          @PathVariable SemesterType semester) {
        Long userId = extractUserIdFromToken(token);
        double income = calculationService.calculateSemesterIncomeWithVacationPay(userId, year, semester);
        return ResponseEntity.ok(income);
    }

//räknar ut vad användaren tjänat på ett år.
    @GetMapping("/yearly-income/{year}")
    public ResponseEntity<Double> calculateYearlyIncome(@RequestHeader("Authorization") String token,
                                                        @PathVariable int year) {
        Long userId = extractUserIdFromToken(token);
        double income = calculationService.calculateYearlyIncome(userId, year);
        return ResponseEntity.ok(income);
    }

//oanvändbar metod pga kan ej hämta workedhourid i frontend.....
    // Räknar ut vad användaren tjänat ett arbetspass.
    @GetMapping("/shift-income")
    public ResponseEntity<Double> calculateShiftIncome(@RequestHeader("Authorization") String token, @RequestParam long workedHoursId) {
        Long userId = extractUserIdFromToken(token);
        double income = calculationService.calculateShiftIncome(userId, workedHoursId);
        return ResponseEntity.ok(income);
    }

    // Jämför inkomsten med fribeloppet. VIKTIGASTE METODEN!
    @GetMapping("/compare-income/{year}/{semesterType}")
    public ResponseEntity<String> compareSemesterIncome(@RequestHeader("Authorization") String token,
                                                        @PathVariable int year,
                                                        @PathVariable SemesterType semesterType) {
        Long userId = extractUserIdFromToken(token);
        String result = calculationService.compareSemesterIncome(userId, year, semesterType);
        return ResponseEntity.ok(result);
    }

    // Hjälpfunktion för att extrahera användar-ID från JWT-token
    private Long extractUserIdFromToken(String token) {
        String jwtToken = token.substring(7); // Ta bort "Bearer " från token
        return jwtTokenProvider.extractUserId(jwtToken);
    }
}
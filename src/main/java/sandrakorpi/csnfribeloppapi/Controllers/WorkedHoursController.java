package sandrakorpi.csnfribeloppapi.Controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/worked-hours")
public class WorkedHoursController {
    private final WorkedHoursService workedHoursService;
    private final SemesterService semesterService;
    private final UserService userService;
    private final CalculationService calculationService;
    private final JwtTokenProvider jwtTokenProvider;

    public WorkedHoursController(WorkedHoursService workedHoursService, SemesterService semesterService, UserService userService, CalculationService calculationService, JwtTokenProvider jwtTokenProvider) {
        this.workedHoursService = workedHoursService;
        this.semesterService = semesterService;
        this.userService = userService;
        this.calculationService = calculationService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //Ger en lista på alla arbetade timmar. Kanske inte något en användare vill ha.
    //Passande för admin eller inte alls?
    @GetMapping("/allTotal")
    private ResponseEntity<List<WorkedHoursDto>> getAllHours (@Parameter(description = "Bearer token", required = true)@RequestHeader("Authorization") String token)
    {
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        List<WorkedHoursDto> workedHoursDtoList = workedHoursService.getWorkedHoursForUser(userId);

        return ResponseEntity.ok(workedHoursDtoList);
    }
//Ger timmar arbetade per termin!
    @GetMapping("/totalForSemester/year/{year}/semestertype/{semesterType}/")
    public ResponseEntity<Double> getHoursBySemester(@PathVariable SemesterType semesterType, int year, @RequestHeader("Authorization")String token)
    {
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        double total = workedHoursService.getWorkedHoursForYearSemester(userId, year, semesterType);
        return ResponseEntity.ok(total);
    }
//Ger totala inkomsten för den månad som användaren söker på.
    @GetMapping("/year/{year}/month/{month}")
    public ResponseEntity<Double> getHoursByYearMonth (@PathVariable int year, @PathVariable int month,@Parameter(description = "Bearer token", required = true) @RequestHeader ("Authorization") String token)
    {
        //jwttoken kommer med barrier, substring -7 för att ta bort det och endast få token.
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
       double total = workedHoursService.getWorkedHoursByYearMonth(userId,year, month);
       return ResponseEntity.ok(total);
    }

    //Hämtar specifika timmar utifrån datum.
    @GetMapping("/year/{year}/month/{month}/date/{date}")
    public ResponseEntity<Double> getHoursByYearMonthAndDate (@PathVariable int year, @PathVariable int month, @PathVariable int date,@Parameter(description = "Bearer token", required = true) @RequestHeader("Authorization") String token)
    {
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        double total = workedHoursService.getWorkedHoursByYearMonthDate(userId,year, month, date);
        return ResponseEntity.ok(total);
    }

    //Hämtar totala timmarna för en termin.
    @GetMapping("/year/{year}")
    public ResponseEntity<Double> getHoursByYear(@PathVariable int year, @Parameter(description = "Bearer token", required = true) @RequestHeader ("Authorization") String token)
    {

        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        double totalHours = workedHoursService.getWorkedHoursByYear(year, userId);
        return ResponseEntity.ok(totalHours);
    }

    //registrerar timmar för användaren.
    @PostMapping("/addHours")
    public ResponseEntity<WorkedHoursDto> addWorkedHours(@RequestBody WorkedHoursDto workedHoursDto)
    {
       WorkedHoursDto savedHours = workedHoursService.saveWorkedHours(workedHoursDto);
       return ResponseEntity.status(HttpStatus.CREATED).body(savedHours);
    }
//Tar bort specifikt arbetspass via id, vet ej hur användbar metod.. Kanske admin?
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id,@Parameter(description = "Bearer token", required = true) @RequestHeader ("Authorization") String token)
    {
        //jwttoken kommer med barrier, substring -7 för att ta bort det och endast få token.
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);

        workedHoursService.deleteHours(userId, id);
        return ResponseEntity.noContent().build();
    }

    //Tar bort timmarna för ett specifikt datum i vald månad.
    @DeleteMapping("/year/{year}/month/{month}/date/{date}")
    public ResponseEntity<Void> deleteByDate(@PathVariable int year, @PathVariable int month, @PathVariable int date,@Parameter(description = "Bearer token", required = true) @RequestHeader ("Authorization") String token) {

        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        workedHoursService.deleteHoursByMonthDate(userId,year, month, date);
        return ResponseEntity.noContent().build();
    }
//Tar bort alla timmarna i vald månad.
    @DeleteMapping("/year/{year}/month/{month}")
    public ResponseEntity<Void> deleteByMonth(@PathVariable int year, @PathVariable int month,@Parameter(description = "Bearer token", required = true) @RequestHeader ("Authorization") String token) {
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        workedHoursService.deleteHoursByYearMonth(userId, year, month);
        return ResponseEntity.noContent().build();
    }

    //Raderar timmar för ett helt år. Är det anävndbart?
    @DeleteMapping("/year/{year}")
    public ResponseEntity<Void> deleteByYear(@PathVariable int year,@Parameter(description = "Bearer token", required = true) @RequestHeader ("Authorization")String token) {
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        workedHoursService.deleteByYear(userId, year);
        return ResponseEntity.noContent().build();
    }

    //Ska kunna ta in år, månad, eller datum för att leta upp timmar att uppdatera.
    @PutMapping("/worked-hours/{id}/month/{month}/date/{date}")
    public ResponseEntity<WorkedHoursDto> updateWorkedHours(@PathVariable Long id,@Parameter(description = "Bearer token", required = true)
                                                            @RequestHeader ("Authorization") String token,
                                            @PathVariable int month,
                                            @PathVariable int date,
                                            @RequestBody WorkedHoursDto workedHoursDto) {
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        return ResponseEntity.ok(workedHoursService.updateWorkedHours(id, month, date, workedHoursDto, userId));
    }

    //Räknar ut hur mycket en användare tjänat en månad.
    @GetMapping("/monthly-income")
    public ResponseEntity<Double> calculateMonthlyIncome(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month) {
        double income = calculationService.calculateMonthlyIncome(userId, year, month);
        return ResponseEntity.ok(income);
    }

    //räknar ut vad användaren tjänat en termin
    @GetMapping("/semester-income")
    public ResponseEntity<Double> calculateSemesterIncome(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam SemesterType semesterType) {
        double income = calculationService.calculateSemesterIncome(userId, year, semesterType);
        return ResponseEntity.ok(income);
    }

    //Räknar ut vad användaren tjänat ett år
    @GetMapping("/yearly-income")
    public ResponseEntity<Double> calculateYearlyIncome(
            @RequestParam long userId,
            @RequestParam int year) {
        double income = calculationService.calculateYearlyIncome(userId, year);
        return ResponseEntity.ok(income);
    }

    //räknar ut vad användaren tjänat ett arbetspass.
    @GetMapping("/shift-income")
    public ResponseEntity<Double> calculateShiftIncome(
            @RequestParam long userId,
            @RequestParam long workedHoursId) {
        double income = calculationService.calculateShiftIncome(userId, workedHoursId);
        return ResponseEntity.ok(income);
    }

    //jämför inkomsten med fribeloppet. VIKTIGASTE METODEN!
    @GetMapping("/{userId}/compare-income")
    public ResponseEntity<String> compareSemesterIncome(
            @PathVariable long userId,
            @RequestParam int year,
            @RequestParam SemesterType semesterType) {

        double workedhours = workedHoursService.getWorkedHoursForYearSemester(userId, year, semesterType);

        // Utför beräkningen baserat på workedHoursList
        String result = calculationService.compareSemesterIncome(userId, year, semesterType);
        return ResponseEntity.ok(result);

}}

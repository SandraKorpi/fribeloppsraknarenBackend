package sandrakorpi.csnfribeloppapi.Controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sandrakorpi.csnfribeloppapi.Dtos.WorkedHoursDto;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import sandrakorpi.csnfribeloppapi.Security.JwtTokenProvider;
import sandrakorpi.csnfribeloppapi.Services.WorkedHoursService;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/worked-hours")
public class WorkedHoursController {
    private final WorkedHoursService workedHoursService;
    private final JwtTokenProvider jwtTokenProvider;

    public WorkedHoursController(WorkedHoursService workedHoursService, JwtTokenProvider jwtTokenProvider) {
        this.workedHoursService = workedHoursService;
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
//Ger inkomster per termin! Viktigaste metoden!!
    @GetMapping("/totalForSemester/{semesterType}/{year}")
    public ResponseEntity<Double> getHoursBySemester(@PathVariable SemesterType semesterType, int year, @RequestHeader("Authorization")String token)
    {
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        double total = workedHoursService.getWorkedHoursForSemester(userId, semesterType, year);
        return ResponseEntity.ok(total);
    }
//Ger totala inkomsten för den månad som användaren söker på.
    @GetMapping("/month/{month}")
    public ResponseEntity<Double> getHoursByMonth (@PathVariable int month,@Parameter(description = "Bearer token", required = true) @RequestHeader ("Authorization") String token)
    {
        //jwttoken kommer med barrier, substring -7 för att ta bort det och endast få token.
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
       double total = workedHoursService.getWorkedHoursByMonth(userId, month);
       return ResponseEntity.ok(total);
    }

    //Hämtar specifika timmar utifrån datum.
    @GetMapping("/month/{month}/date/{date}")
    public ResponseEntity<Double> getHoursByMonthAndDate (@PathVariable int month, @PathVariable int date,@Parameter(description = "Bearer token", required = true) @RequestHeader("Authorization") String token)
    {
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        double total = workedHoursService.getWorkedHoursByMonthDate(userId, month, date);
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
    @DeleteMapping("/month/{month}/date/{date}")
    public ResponseEntity<Void> deleteByDate(@PathVariable int month, @PathVariable int date,@Parameter(description = "Bearer token", required = true) @RequestHeader ("Authorization") String token) {

        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        workedHoursService.deleteHoursByMonthDate(userId, month, date);
        return ResponseEntity.noContent().build();
    }
//Tar bort alla timmarna i vald månad.
    @DeleteMapping("/month/{month}")
    public ResponseEntity<Void> deleteByMonth(@PathVariable int month,@Parameter(description = "Bearer token", required = true) @RequestHeader ("Authorization") String token) {
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        workedHoursService.deleteHoursByMonth(userId, month);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/year/{year}")
    public ResponseEntity<Void> deleteByYear(@PathVariable int year,@Parameter(description = "Bearer token", required = true) @RequestHeader ("Authorization")String token) {
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        workedHoursService.deleteByYear(userId, year);
        return ResponseEntity.noContent().build();
    }

    //Ska kunna ta in pr, månad, eller datum för att leta upp timmar att uppdatera.
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
}

package sandrakorpi.csnfribeloppapi.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sandrakorpi.csnfribeloppapi.Dtos.WorkedHoursDto;
import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import sandrakorpi.csnfribeloppapi.Services.WorkedHoursService;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/worked-hours")
public class WorkedHoursController {
    private final WorkedHoursService workedHoursService;

    public WorkedHoursController(WorkedHoursService workedHoursService) {
        this.workedHoursService = workedHoursService;
    }

    //Ger en lista på alla arbetade timmar. Kanske inte något en användare vill ha.
    //Passande för admin eller inte alls?
    @GetMapping("/allTotal")
    private ResponseEntity<List<WorkedHoursDto>> getAllHours ()
    {
        List<WorkedHoursDto> workedHoursDtoList = workedHoursService.getAllWorkedHours();

        return ResponseEntity.ok(workedHoursDtoList);
    }

//Ger totala inkomsten för den månad som användaren söker på.
    @GetMapping("/month/{month}")
    public ResponseEntity<Double> getHoursByMonth (@PathVariable int month)
    {
double total = workedHoursService.getWorkedHoursByMonth(month);
return ResponseEntity.ok(total);
    }

    //Hämtar specifika timmar utifrån datum.
    @GetMapping("/month/{month}/date/{date}")
    public ResponseEntity<Double> getHoursByMonthAndDate (@PathVariable int month, @PathVariable int date)
    {
        double total = workedHoursService.getWorkedHoursByMonthDate(month, date);

       return ResponseEntity.ok(total);
    }

    //Hämtar totala timmarna för en termin.
    @GetMapping("/year/{year}")
    public ResponseEntity<Double> getHoursByYear(@PathVariable int year)
    {
        double totalHours = workedHoursService.getWorkedHoursByYear(year);
        return ResponseEntity.ok(totalHours);
    }

    @PostMapping("/addHours")
    public ResponseEntity<WorkedHoursDto> addWorkedHours(@RequestBody WorkedHoursDto workedHoursDto)
    {
        System.out.println("Received WorkedHoursDto: " + workedHoursDto);
       return ResponseEntity.status(201).body(workedHoursService.saveWorkedHours(workedHoursDto));
    }
//Tar bort specifikt arbetspass via id, vet ej hur användbar metod.. Kanske admin?
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id)
    {
        workedHoursService.deleteHours(id);
        return ResponseEntity.noContent().build();
    }

    //Tar bort timmarna för ett specifikt datum i vald månad.
    @DeleteMapping("/month/{month}/date/{date}")
    public ResponseEntity<Void> deleteByDate(@PathVariable int month, @PathVariable int date) {
        workedHoursService.deleteHoursByMonthDate(month, date);
        return ResponseEntity.noContent().build();
    }
//Tar bort alla timmarna i vald månad.
    @DeleteMapping("/month/{month}")
    public ResponseEntity<Void> deleteByMonth(@PathVariable int month) {
        workedHoursService.deleteHoursByMonth(month);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/year/{year}")
    public ResponseEntity<Void> deleteByYear(@PathVariable int year) {
        workedHoursService.deleteByYear(year);
        return ResponseEntity.noContent().build();
    }

    //Ska kunna ta in pr, månad, eller datum för att leta upp timmar att uppdatera.
    @PutMapping("/worked-hours/{id}/month/{month}/date/{date}")
    public ResponseEntity<WorkedHoursDto> updateWorkedHours(@PathVariable Long id,
                                            @PathVariable int month,
                                            @PathVariable int date,
                                            @RequestBody WorkedHoursDto workedHoursDto) {
        return ResponseEntity.ok(workedHoursService.updateWorkedHours(id, month, date, workedHoursDto));
    }
}

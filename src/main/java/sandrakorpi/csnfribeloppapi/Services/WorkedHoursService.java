package sandrakorpi.csnfribeloppapi.Services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sandrakorpi.csnfribeloppapi.Dtos.UserDto;
import sandrakorpi.csnfribeloppapi.Dtos.WorkedHoursDto;
import sandrakorpi.csnfribeloppapi.Enums.Role;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Exceptions.ResourceNotFoundException;
import sandrakorpi.csnfribeloppapi.Exceptions.UserAlreadyExistsException;
import sandrakorpi.csnfribeloppapi.Models.Semester;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import sandrakorpi.csnfribeloppapi.Repositories.SemesterRepository;
import sandrakorpi.csnfribeloppapi.Repositories.UserRepository;
import sandrakorpi.csnfribeloppapi.Repositories.WorkedHoursRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class WorkedHoursService {

    private final WorkedHoursRepository workedHoursRepository;
    private final SemesterService semesterService;
    private final UserService userService;

    public WorkedHoursService(WorkedHoursRepository workedHoursRepository, SemesterService semesterService, UserService userService) {
        this.workedHoursRepository = workedHoursRepository;
        this.semesterService = semesterService;

        this.userService = userService;
    }
    public List<WorkedHoursDto> getWorkedHoursForUser(long userId) {
        List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_Id(userId);
        List<WorkedHoursDto> workedHoursDtoList = new ArrayList<>();

        for (WorkedHours workedHours : workedHoursList) {
            workedHoursDtoList.add(convertToDto(workedHours));
        }

        return workedHoursDtoList;
    }
        @Transactional
    public WorkedHoursDto saveWorkedHours(WorkedHoursDto workedHoursDto) {
            // Hämta användaren baserat på userId
            UserDto userDto = userService.findById(workedHoursDto.getUserId());
            User user = userService.convertToUser(userDto);
            //Räknar ut vilken termin inkomsten tillhör.
            Semester semester = semesterService.getSemesterForMonthAndYear(workedHoursDto.getMonth(), workedHoursDto.getYear());
            // Skapa en ny WorkedHours-instans
            WorkedHours workedHours = new WorkedHours();
            workedHours.setHours(workedHoursDto.getHours());
            workedHours.setMonth(workedHoursDto.getMonth());
            workedHours.setDate(workedHoursDto.getDate());
            workedHours.setYear(workedHoursDto.getYear());
            workedHours.setHourlyRate(workedHoursDto.getHourlyRate());
            workedHours.setVacationPay(workedHoursDto.getVacationPay());
            workedHours.setUser(user); // Koppla användaren till WorkedHours
            workedHours.setSemester(semester);//Sätter semester, då det inte tas in från användaren.

            // Spara instansen direkt och returnera DTO:n
            return convertToDto(workedHoursRepository.save(workedHours));
    }

    private double calculateTotalHours(List<WorkedHours> workedHoursList) {
        double totalHours = 0.0;
        for (WorkedHours workedHours : workedHoursList) {
            totalHours += workedHours.getHours();
        }
        return totalHours;
    }
    public double getWorkedHoursByYear(int year, long userId)
    {
        List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndYear(userId, year);
        if (workedHoursList.isEmpty()) {
            throw new ResourceNotFoundException("Inga arbetade timmar hittades för året " + year);}
        return calculateTotalHours(workedHoursList);
    }


    public double getWorkedHoursByYearMonthDate(long userId, int year, int month, int date) {
        Optional<WorkedHours> workedHours = workedHoursRepository.findByUser_IdAndYearAndMonthAndDate(userId, year, month, date);
        if (workedHours.isPresent()) {
            WorkedHoursDto workedHoursDto = convertToDto(workedHours.get());
            return workedHoursDto.getHours();
        } else {
            throw new ResourceNotFoundException("Inga arbetade timmar hittades för " + date + " " + month + " " + year);
        }
    }


    //Ska hämta timmarna arbetade per termin. Viktig!!!
    public double getWorkedHoursForYearSemester(long userId, int year, SemesterType semesterType) {
        List<WorkedHoursDto> workedHoursDtos = new ArrayList<>();
        List<WorkedHours> workedHours = new ArrayList<>();

        // Definiera vilka månader som tillhör respektive termin
        List<Integer> months = new ArrayList<>();
        if (semesterType == SemesterType.VT) {
            months.addAll(Arrays.asList(1, 2, 3, 4, 5, 6)); // Vårtermin (VT)
        } else if (semesterType == SemesterType.HT) {
            months.addAll(Arrays.asList(7, 8, 9, 10, 11, 12)); // Hösttermin (HT)
        }

        // Hämta arbetade timmar för de specifika månaderna
        for (Integer month : months) {
            List<WorkedHours> hoursForMonth = workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, month);
            workedHours.addAll(hoursForMonth);
        }
        return calculateTotalHours(workedHours);
    }

    public double getWorkedHoursByYearMonth (long userId, int year, int month)
    {
        List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndYearAndMonth(userId,year, month);
        if (workedHoursList.isEmpty()) {
            throw new ResourceNotFoundException("Inga arbetade timmar hittades för " + month);}

        return calculateTotalHours(workedHoursList);
    }
    public String getWorkedHoursDetailsByYearMonth(long userId, int year, int month) {
        List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, month);
        if (workedHoursList.isEmpty()) {
            throw new ResourceNotFoundException("Inga arbetade timmar hittades för " + month);
        }

        StringBuilder details = new StringBuilder();
        details.append("Detaljer för ").append(year).append("-").append(month).append(":\n");

        for (WorkedHours workedHours : workedHoursList) {
            details.append("Datum: ").append(workedHours.getDate())
                    .append(", Timmar: ").append(workedHours.getHours())
                    .append(", Timlön: ").append(workedHours.getHourlyRate())
                    .append(", Semesterersättning: ").append(workedHours.getVacationPay())
                    .append("\n");
        }

        return details.toString();
    }
    public WorkedHoursDto convertToDto(WorkedHours workedHours){
        WorkedHoursDto workedHoursDto = new WorkedHoursDto();
        workedHoursDto.setUserId(workedHours.getUser().getId());

        workedHoursDto.setYear(workedHours.getYear());
        workedHoursDto.setMonth(workedHours.getMonth());
        workedHoursDto.setDate(workedHours.getDate());
        workedHoursDto.setHours(workedHours.getHours());
        workedHoursDto.setHourlyRate(workedHours.getHourlyRate());
        workedHoursDto.setVacationPay(workedHours.getVacationPay());
        return workedHoursDto;
    }

    @Transactional
public WorkedHoursDto updateWorkedHours(long id, int month, int date, WorkedHoursDto workedHoursDto, long userId){
    WorkedHours workedHoursToUpdate = workedHoursRepository.findByIdAndUser_Id(id, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Du kan inte komma åt andras timmar!"));
    // Hämta användaren baserat på userId från workedHoursDto

        User user = userService.getUserEntityById(userId); // Använd den nya metoden för att hämta User-entity
        workedHoursToUpdate.setUser(user);
    // Uppdatera övriga fält i det befintliga objektet
    workedHoursToUpdate.setYear(workedHoursDto.getYear());
    workedHoursToUpdate.setMonth(month);
    workedHoursToUpdate.setDate(date);
    workedHoursToUpdate.setHourlyRate(workedHoursDto.getHourlyRate());
    workedHoursToUpdate.setHours(workedHoursDto.getHours());
    workedHoursToUpdate.setVacationPay(workedHoursDto.getVacationPay());

    // Spara de uppdaterade timmarna
    return convertToDto(workedHoursRepository.save(workedHoursToUpdate));
}
//uppdetera workedhours genom att ange userid ist för workedhoursid
public WorkedHoursDto updateWorkedHoursByUserId(Long userId, int year,  int month, int date, WorkedHoursDto workedHoursDto) {
    Optional<WorkedHours> existingWorkedHours = workedHoursRepository.findByUser_IdAndYearAndMonthAndDate(userId, year, month, date);

    if (existingWorkedHours.isPresent()) {
        WorkedHours workedHours = existingWorkedHours.get();
        workedHours.setHours(workedHoursDto.getHours());
        workedHours.setHourlyRate(workedHoursDto.getHourlyRate());
        workedHours.setVacationPay(workedHoursDto.getVacationPay());
        workedHoursRepository.save(workedHours);
        return convertToDto(workedHours);
    } else {
        throw new ResourceNotFoundException("Inga arbetade timmar hittades för användare, månad och datum.");
    }
}


    @Transactional
public void deleteHours (long userId, long id)
{
    Optional<WorkedHours> workedHoursToDelete = workedHoursRepository.findByIdAndUser_Id(userId, id);
    if (workedHoursToDelete.isPresent()) {
        workedHoursRepository.delete(workedHoursToDelete.get());
    } else {
        throw new ResourceNotFoundException("Inga arbetade timmar hittades med id: " + id);
    }
}
@Transactional
public void deleteByYear (long userId, int year)
{
    //Hämtar från databasen i en lista, går igenom listan och raderar alla timmar.
    List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndYear(userId, year);

    workedHoursRepository.deleteAll(workedHoursList);
}
@Transactional
public void deleteHoursByYearMonth (long userId, int year, int month)
{
    //Hämtar från databasen i en lista, går igenom listan och raderar alla timmar.
    List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndYearAndMonth(userId, year, month);

    workedHoursRepository.deleteAll(workedHoursList);
}

    @Transactional
    public void deleteHoursByMonthDate(long userId, int year, int month, int date) {
        Optional<WorkedHours> workedHours = workedHoursRepository.findByUser_IdAndYearAndMonthAndDate(userId, year, month, date);

        if (workedHours.isEmpty()) {
            throw new ResourceNotFoundException("Inga arbetade timmar hittades för användare med id " + userId + " för datum " + date + "/" + month + "/" + year);
        }
        WorkedHours deleteHour = workedHours.get();
        workedHoursRepository.delete(deleteHour);
    }


//Dessa metoder används inte längre. Eventuellt raderas??

    public WorkedHours getHoursOrFail(long id) {
        return workedHoursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Det finns inga arbetade timmar med id:  " + id));
    }

    public boolean isOwnedByUser(long workedHoursId, long userId) {
        // Hämtar arbetstimman från databasen och kontrollerar att `userId` matchar
        return workedHoursRepository.findById(workedHoursId)
                .map(workedHours -> workedHours.getUserId().equals(userId))
                .orElse(false);
    }
    //Hämtar alla timmar totalt för en användare. Kanske onödigt?
    public List<WorkedHoursDto> getAllWorkedHours(){

        List<WorkedHours> workedHoursList = workedHoursRepository.findAll();
        if (workedHoursList.isEmpty()) {
            throw new ResourceNotFoundException("Inga arbetade timmar hittades");}
        //överför alla workedhours till lista med workedhoursdto.
        List<WorkedHoursDto> workedHoursDtoList = new ArrayList<>();
        for(WorkedHours workedHours : workedHoursList){
            workedHoursDtoList.add(convertToDto(workedHours));
        }
        return workedHoursDtoList;
    }

}

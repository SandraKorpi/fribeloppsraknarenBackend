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
import sandrakorpi.csnfribeloppapi.Exceptions.ResourceNotFoundException;
import sandrakorpi.csnfribeloppapi.Exceptions.UserAlreadyExistsException;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import sandrakorpi.csnfribeloppapi.Repositories.UserRepository;
import sandrakorpi.csnfribeloppapi.Repositories.WorkedHoursRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkedHoursService {

    private final WorkedHoursRepository workedHoursRepository;
private final UserService userService;

    public WorkedHoursService(WorkedHoursRepository workedHoursRepository, UserService userService) {
        this.workedHoursRepository = workedHoursRepository;
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

            // Skapa en ny WorkedHours-instans
            WorkedHours workedHours = new WorkedHours();
            workedHours.setHours(workedHoursDto.getHours());
            workedHours.setMonth(workedHoursDto.getMonth());
            workedHours.setDate(workedHoursDto.getDate());
            workedHours.setYear(workedHoursDto.getYear());
            workedHours.setUser(user); // Koppla användaren till WorkedHours

            // Spara instansen direkt och returnera DTO:n
            return convertToDto(workedHoursRepository.save(workedHours));
    }


    public double getWorkedHoursByYear(int year, long userId)
    {
        List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndYear(userId, year);
        if (workedHoursList.isEmpty()) {
            throw new ResourceNotFoundException("Inga arbetade timmar hittades för året " + year);}
        return calculateTotalHours(workedHoursList);
    }

    public double getWorkedHoursByMonthDate (long userId, int month, int date)
    {
        List <WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndMonthAndDate(userId, month, date);
        if (workedHoursList.isEmpty()) {
            throw new ResourceNotFoundException("Inga arbetade timmar hittades för " + date +" "+ month);}
        return calculateTotalHours(workedHoursList);

    }

    public double getWorkedHoursByMonth (long userId, int month)
    {
        List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndMonth(userId, month);
        if (workedHoursList.isEmpty()) {
            throw new ResourceNotFoundException("Inga arbetade timmar hittades för " + month);}

        return calculateTotalHours(workedHoursList);
    }

    //En metod som räknar ut sammanlagda timmarna, att använda i metoderna där det behövs.
    private double calculateTotalHours(List<WorkedHours> workedHoursList) {
        double totalHours = 0.0;
        for (WorkedHours workedHours : workedHoursList) {
            totalHours += workedHours.getHours();
        }
        return totalHours;
    }


    public WorkedHoursDto convertToDto(WorkedHours workedHours){
        WorkedHoursDto workedHoursDto = new WorkedHoursDto();
        workedHoursDto.setUserId(workedHours.getUser().getId());

        workedHoursDto.setYear(workedHours.getYear());
        workedHoursDto.setMonth(workedHours.getMonth());
        workedHoursDto.setDate(workedHours.getDate());
        workedHoursDto.setHours(workedHours.getHours());

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
    workedHoursToUpdate.setHours(workedHoursDto.getHours());

    // Spara de uppdaterade timmarna
    return convertToDto(workedHoursRepository.save(workedHoursToUpdate));
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
public void deleteHoursByMonth (long userId, int month)
{
    //Hämtar från databasen i en lista, går igenom listan och raderar alla timmar.
    List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndMonth(userId, month);

    workedHoursRepository.deleteAll(workedHoursList);
}
@Transactional
public void deleteHoursByMonthDate(long userId, int month, int date)
{
    //Hämtar från databasen i en lista, går igenom listan och raderar alla timmar.
    List<WorkedHours> workedHoursList = workedHoursRepository.findByUser_IdAndMonthAndDate(userId, month, date);
    workedHoursRepository.deleteAll(workedHoursList);
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

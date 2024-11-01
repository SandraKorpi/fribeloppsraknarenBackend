package sandrakorpi.csnfribeloppapi.Services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
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

@Service
public class WorkedHoursService {

    private final WorkedHoursRepository workedHoursRepository;
private final UserService userService;

    public WorkedHoursService(WorkedHoursRepository workedHoursRepository, UserService userService) {
        this.workedHoursRepository = workedHoursRepository;
        this.userService = userService;
    }

    @Transactional
    public WorkedHoursDto saveWorkedHours(WorkedHoursDto workedHoursDto) {
        try {
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

            // Spara instansen direkt
            WorkedHours savedWorkedHours = workedHoursRepository.save(workedHours);

            // Returnera DTO:n
            return convertToDto(savedWorkedHours);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException("User not found: " + e.getMessage());
        }
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

    public double getWorkedHoursByYear(int year)
    {
        List<WorkedHours> workedHoursList = workedHoursRepository.findByYear(year);
        if (workedHoursList.isEmpty()) {
            throw new ResourceNotFoundException("Inga arbetade timmar hittades för året " + year);}
        return calculateTotalHours(workedHoursList);
    }

    public double getWorkedHoursByMonthDate (int month, int date)
    {
        List <WorkedHours> workedHoursList = workedHoursRepository.findByMonthAndDate(month, date);
        if (workedHoursList.isEmpty()) {
            throw new ResourceNotFoundException("Inga arbetade timmar hittades för " + date +" "+ month);}
        return calculateTotalHours(workedHoursList);

    }

    public double getWorkedHoursByMonth (int month)
    {
        List<WorkedHours> workedHoursList = workedHoursRepository.findByMonth(month);
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

public WorkedHoursDto updateWorkedHours(Long id, int month, int date, WorkedHoursDto workedHoursDto){
    WorkedHours workedHoursToUpdate = workedHoursRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Arbetade timmar hittades inte"));

    // Hämta användaren baserat på userId från workedHoursDto
    Long userId = workedHoursDto.getUserId();
    if (userId != null) {
        User user = userService.getUserEntityById(userId); // Använd den nya metoden för att hämta User-entity
        workedHoursToUpdate.setUser(user);
    }

    // Uppdatera övriga fält i det befintliga objektet
    workedHoursToUpdate.setYear(workedHoursDto.getYear());
    workedHoursToUpdate.setMonth(month);
    workedHoursToUpdate.setDate(date);
    workedHoursToUpdate.setHours(workedHoursDto.getHours());

    // Spara de uppdaterade timmarna
    return convertToDto(workedHoursRepository.save(workedHoursToUpdate));
}

public void deleteHours (long id)
{
  WorkedHours workedHoursToDelete =  getHoursOrFail(id);
  workedHoursRepository.delete(workedHoursToDelete);

}
public void deleteByYear (int year)
{
    //Hämtar från databasen i en lista, går igenom listan och raderar alla timmar.
    List<WorkedHours> workedHoursList = workedHoursRepository.findByYear(year);

    workedHoursRepository.deleteAll(workedHoursList);
}
public void deleteHoursByMonth (int month)
{
    //Hämtar från databasen i en lista, går igenom listan och raderar alla timmar.
    List<WorkedHours> workedHoursList = workedHoursRepository.findByMonth(month);

    workedHoursRepository.deleteAll(workedHoursList);
}

public void deleteHoursByMonthDate(int month, int date)
{
    //Hämtar från databasen i en lista, går igenom listan och raderar alla timmar.
    List<WorkedHours> workedHoursList = workedHoursRepository.findByMonthAndDate(month, date);
    workedHoursRepository.deleteAll(workedHoursList);
}
    public WorkedHours getHoursOrFail(long id) {
        return workedHoursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Det finns inga arbetade timmar med id:  " + id));
    }
}

package sandrakorpi.csnfribeloppapi.Services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sandrakorpi.csnfribeloppapi.Dtos.UserDto;
import sandrakorpi.csnfribeloppapi.Dtos.WorkedHoursDto;
import sandrakorpi.csnfribeloppapi.Enums.Role;
import sandrakorpi.csnfribeloppapi.Exceptions.ResourceNotFoundException;
import sandrakorpi.csnfribeloppapi.Exceptions.UserAlreadyExistsException;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import sandrakorpi.csnfribeloppapi.Repositories.WorkedHoursRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkedHoursService {
    private  final WorkedHoursRepository workedHoursRepository;
private final UserService userService;
    public WorkedHoursService(WorkedHoursRepository workedHoursRepository, UserService userService) {
        this.workedHoursRepository = workedHoursRepository;
        this.userService = userService;
    }

    public WorkedHours saveWorkedHours(WorkedHoursDto workedHoursDto) {
        // Konvertera WorkedHoursDto till WorkedHours
        WorkedHours workedHours = convertToEntity(workedHoursDto);

        // Spara workedHours till databasen
        return workedHoursRepository.save(workedHours);
    }

    private WorkedHours convertToEntity(WorkedHoursDto dto) {
        WorkedHours workedHours = new WorkedHours();

        // Hämta UserDto från någon källa eller anta att den kommer med DTO:n
        UserDto userDto = new UserDto();
        // Här kan du sätta värden på userDto om det är nödvändigt
        // Exempel: userDto.setUserName("someUserName");

        // Konvertera UserDto till User
        User user = userService.convertToUser(userDto); // Använd metoden för att konvertera

        // Sätt user i workedHours
        workedHours.setUser(user);
        workedHours.setHours(dto.getHours());
        workedHours.setMonth(dto.getMonth());
        workedHours.setYear(dto.getYear());

        return workedHours;
    }

    public List<WorkedHoursDto> getAllWorkedHours(){
        List<WorkedHours> workedHoursList = workedHoursRepository.findAll();
        //överför alla workedhours till lista med workedhoursdto.
        List<WorkedHoursDto> workedHoursDtoList = new ArrayList<>();
        for(WorkedHours workedHours : workedHoursList){
            workedHoursDtoList.add(convertoToDto(workedHours));
        }

                    return workedHoursDtoList;
    }

    public WorkedHoursDto convertoToDto(WorkedHours workedHours){
        WorkedHoursDto workedHoursDto = new WorkedHoursDto();
        workedHoursDto.setId(workedHours.getId());
        UserDto userDto = userService.convertToUserDto(workedHours.getUser());
        workedHoursDto.setUser(userDto);
        workedHoursDto.setYear(workedHours.getYear());
        workedHoursDto.setMonth(workedHours.getMonth());
        workedHoursDto.setHours(workedHours.getHours());
        return workedHoursDto;


    }
public WorkedHours updateWorkedHours(Long id, WorkedHoursDto workedHoursDto){
    WorkedHours workedHoursToUpdate = workedHoursRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Arbetade timmar hittades inte"));

    // Uppdatera fälten i det befintliga objektet
    workedHoursToUpdate.setUser(userService.convertToUser(workedHoursDto.getUser())); // Om du vill ändra användaren
    workedHoursToUpdate.setYear(workedHoursDto.getYear());
    workedHoursToUpdate.setMonth(workedHoursDto.getMonth());
    workedHoursToUpdate.setHours(workedHoursDto.getHours());

    // Spara de uppdaterade timmarna
    return workedHoursRepository.save(workedHoursToUpdate);
}

public void deleteHours (long id)
{
  WorkedHours workedHoursToDelete =  getHoursOrFail(id);
  workedHoursRepository.delete(workedHoursToDelete);

}
    public WorkedHours getHoursOrFail(long id) {
        return workedHoursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Det finns inga arbetade timmar med id:  " + id));
    }
}

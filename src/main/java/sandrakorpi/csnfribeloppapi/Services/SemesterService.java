package sandrakorpi.csnfribeloppapi.Services;

import org.springframework.stereotype.Service;
import sandrakorpi.csnfribeloppapi.Dtos.SemesterDto;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Exceptions.ResourceNotFoundException;
import sandrakorpi.csnfribeloppapi.Models.Semester;
import sandrakorpi.csnfribeloppapi.Repositories.SemesterRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class SemesterService {

    private final SemesterRepository semesterRepository;


    public SemesterService(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }
    //Anger vilken termin timmarna tillhör. Ska ske i backend utan att användaren gör något.
    public Semester getSemesterForMonthAndYear(int month, int year) {
        SemesterType semesterType = (month >= 1 && month <= 6) ? SemesterType.VT : SemesterType.HT;
        return semesterRepository.findByTypeAndYear(semesterType, year);
    }


public SemesterDto saveSemester (SemesterDto semesterDto)
{
    Semester existingSemester = semesterRepository.findByTypeAndYear(semesterDto.getType(), semesterDto.getYear());

    // Kontrollera om den existerande terminen är null
    if (existingSemester != null) {
        throw new IllegalArgumentException("Det finns redan en termin för " + semesterDto.getType() + " i år " + semesterDto.getYear());
    }

    Semester semester = convertToEntity(semesterDto);
    semesterRepository.save(semester);

    return convertToDto(semester);
}

public void deleteSemester (long id)
{
  semesterRepository.delete(findSemesterOrfail(id));
}

public Semester findSemesterOrfail(long id)
{
    return semesterRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Det finns ingen termin med id:  " + id));
}

public SemesterDto findById(long id)
{
    return convertToDto(findSemesterOrfail(id));
}

public List<SemesterDto> findAll ()
{
    List<Semester> semesterList = semesterRepository.findAll();
    List<SemesterDto> semesterDtoList = new ArrayList<>();
    for(Semester semester: semesterList)
    {
       SemesterDto semesterDto = convertToDto(semester);
        semesterDtoList.add(semesterDto);
    }
    return semesterDtoList;
}

public SemesterDto updateSemester(long id, SemesterDto semesterDto)
{
    Semester updatedSemester = findSemesterOrfail(id);
    updatedSemester.setYear(semesterDto.getYear());
    updatedSemester.setType(semesterDto.getType());
    updatedSemester.setIncomeLimit(semesterDto.getIncomeLimit());
    semesterRepository.save(updatedSemester);
    return convertToDto(updatedSemester);
}

    public Semester convertToEntity(SemesterDto semesterDto) {
        Semester semester = new Semester();
        semester.setIncomeLimit(semesterDto.getIncomeLimit());
        semester.setYear(semesterDto.getYear());
        semester.setType(semesterDto.getType());
        return semester;
    }
public SemesterDto convertToDto (Semester semester)
{
    SemesterDto semesterDto = new SemesterDto();
    semesterDto.setId(semester.getId());
    semesterDto.setYear(semester.getYear());
    semesterDto.setType(semester.getType());
semesterDto.setIncomeLimit(semester.getIncomeLimit());
    List<Integer> months = new ArrayList<>();
    if (semester.getType() == SemesterType.VT) {
        months = Arrays.asList(1, 2, 3, 4, 5, 6); // Januari till Juni
    } else if (semester.getType() == SemesterType.HT) {
        months = Arrays.asList(7, 8, 9, 10, 11, 12); // Juli till December
    }
    semesterDto.setMonths(months);
    return semesterDto;

}

    public List<Integer> getMonthsForSemesterType(SemesterType semesterType) {
        List<Integer> months = new ArrayList<>();
        if (semesterType == SemesterType.VT) {
            months.addAll(Arrays.asList(1, 2, 3, 4, 5, 6)); // Vårtermin
        } else if (semesterType == SemesterType.HT) {
            months.addAll(Arrays.asList(7, 8, 9, 10, 11, 12)); // Hösttermin
        }
        return months;
    }
    }




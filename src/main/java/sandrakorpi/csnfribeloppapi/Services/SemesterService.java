package sandrakorpi.csnfribeloppapi.Services;

import org.springframework.stereotype.Service;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Models.Semester;
import sandrakorpi.csnfribeloppapi.Repositories.SemesterRepository;

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

    public void setSemesterType (int month) {

        SemesterType semesterType = (month >= 1 && month <= 6) ? SemesterType.VT : SemesterType.HT;
    }

}

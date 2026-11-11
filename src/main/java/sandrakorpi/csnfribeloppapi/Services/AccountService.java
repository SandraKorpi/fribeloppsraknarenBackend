package sandrakorpi.csnfribeloppapi.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sandrakorpi.csnfribeloppapi.Exceptions.ResourceNotFoundException;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Repositories.StudyPeriodRepository;
import sandrakorpi.csnfribeloppapi.Repositories.UserRepository;
import sandrakorpi.csnfribeloppapi.Repositories.WorkedHoursRepository;

/**
 * Raderar ett konto tillsammans med allt som hör till det.
 * Apple kräver att appar som låter användare skapa konto också låter dem radera kontot i appen.
 */
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final WorkedHoursRepository workedHoursRepository;
    private final StudyPeriodRepository studyPeriodRepository;

    public AccountService(UserRepository userRepository,
                          WorkedHoursRepository workedHoursRepository,
                          StudyPeriodRepository studyPeriodRepository) {
        this.userRepository = userRepository;
        this.workedHoursRepository = workedHoursRepository;
        this.studyPeriodRepository = studyPeriodRepository;
    }

    @Transactional
    public void deleteAccount(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Användare med ID " + userId + " hittades inte"));

        workedHoursRepository.deleteAll(workedHoursRepository.findByUser_Id(userId));
        studyPeriodRepository.deleteByUser_Id(userId);
        userRepository.delete(user);
    }
}

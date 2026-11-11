package sandrakorpi.csnfribeloppapi.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sandrakorpi.csnfribeloppapi.Dtos.FribeloppStatusDto;
import sandrakorpi.csnfribeloppapi.Dtos.StudyPeriodDto;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Fribelopp.FribeloppTable;
import sandrakorpi.csnfribeloppapi.Models.StudyPeriod;
import sandrakorpi.csnfribeloppapi.Repositories.StudyPeriodRepository;

import java.util.Optional;

@Service
public class StudyPeriodService {

    private final StudyPeriodRepository studyPeriodRepository;
    private final UserService userService;

    public StudyPeriodService(StudyPeriodRepository studyPeriodRepository, UserService userService) {
        this.studyPeriodRepository = studyPeriodRepository;
        this.userService = userService;
    }

    /** Fribeloppet som gäller användaren för halvåret, och var siffran kommer ifrån. */
    public record ResolvedFribelopp(long amount, String source, int weeks, int studyRate) {
    }

    @Transactional(readOnly = true)
    public Optional<StudyPeriodDto> find(long userId, int year, SemesterType type) {
        return studyPeriodRepository.findByUser_IdAndYearAndType(userId, year, type)
                .map(this::toDto);
    }

    @Transactional
    public StudyPeriodDto save(long userId, int year, SemesterType type, StudyPeriodDto dto) {
        validate(year, dto);

        StudyPeriod studyPeriod = studyPeriodRepository.findByUser_IdAndYearAndType(userId, year, type)
                .orElseGet(() -> {
                    StudyPeriod created = new StudyPeriod();
                    created.setUser(userService.getUserEntityById(userId));
                    created.setYear(year);
                    created.setType(type);
                    return created;
                });

        studyPeriod.setWeeks(dto.getWeeks());
        studyPeriod.setStudyRate(dto.getStudyRate());
        studyPeriod.setFribeloppOverride(dto.getFribeloppOverride());

        return toDto(studyPeriodRepository.save(studyPeriod));
    }

    @Transactional(readOnly = true)
    public Optional<ResolvedFribelopp> resolve(long userId, int year, SemesterType type) {
        return studyPeriodRepository.findByUser_IdAndYearAndType(userId, year, type)
                .map(period -> {
                    if (period.getFribeloppOverride() != null) {
                        return new ResolvedFribelopp(
                                Math.round(period.getFribeloppOverride()),
                                FribeloppStatusDto.SOURCE_OWN_VALUE,
                                period.getWeeks(),
                                period.getStudyRate());
                    }
                    long amount = FribeloppTable.fribelopp(year, period.getWeeks(), period.getStudyRate());
                    String source = FribeloppTable.isEstimated(year)
                            ? FribeloppStatusDto.SOURCE_ESTIMATED
                            : FribeloppStatusDto.SOURCE_CSN_TABLE;
                    return new ResolvedFribelopp(amount, source, period.getWeeks(), period.getStudyRate());
                });
    }

    private void validate(int year, StudyPeriodDto dto) {
        if (!FribeloppTable.STUDY_RATES.contains(dto.getStudyRate())) {
            throw new IllegalArgumentException("Studietakten måste vara 100, 75 eller 50 procent");
        }
        if (dto.getWeeks() < FribeloppTable.MIN_WEEKS || dto.getWeeks() > FribeloppTable.MAX_WEEKS) {
            throw new IllegalArgumentException("Antal veckor måste vara mellan "
                    + FribeloppTable.MIN_WEEKS + " och " + FribeloppTable.MAX_WEEKS);
        }
        if (dto.getFribeloppOverride() == null && !FribeloppTable.isSupportedYear(year)) {
            throw new IllegalArgumentException("Fribeloppet för " + year
                    + " finns inte inlagt ännu. Ange fribeloppet från ditt CSN-beslut.");
        }
    }

    private StudyPeriodDto toDto(StudyPeriod period) {
        return new StudyPeriodDto(
                period.getYear(),
                period.getType(),
                period.getWeeks(),
                period.getStudyRate(),
                period.getFribeloppOverride());
    }
}

package sandrakorpi.csnfribeloppapi.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Models.StudyPeriod;

import java.util.Optional;

@Repository
public interface StudyPeriodRepository extends JpaRepository<StudyPeriod, Long> {

    Optional<StudyPeriod> findByUser_IdAndYearAndType(Long userId, int year, SemesterType type);

    void deleteByUser_Id(Long userId);
}

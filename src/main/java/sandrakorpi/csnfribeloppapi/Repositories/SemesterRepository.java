package sandrakorpi.csnfribeloppapi.Repositories;

import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Models.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Semester findByType(SemesterType semesterType);

    Semester findByTypeAndYear(SemesterType semesterType, int year);


    Optional<Semester> findByYearAndType(int year, SemesterType type);
}

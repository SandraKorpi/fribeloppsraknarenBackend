package sandrakorpi.csnfribeloppapi.Repositories;

import sandrakorpi.csnfribeloppapi.Models.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
}

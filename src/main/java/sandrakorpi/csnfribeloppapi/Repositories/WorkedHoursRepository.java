package sandrakorpi.csnfribeloppapi.Repositories;

import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkedHoursRepository extends JpaRepository<WorkedHours, Long> {
}

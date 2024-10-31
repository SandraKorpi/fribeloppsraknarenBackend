package sandrakorpi.csnfribeloppapi.Repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkedHoursRepository extends JpaRepository<WorkedHours, Long> {
    List<WorkedHours> findByMonthAndDate(int month, int date);
    List<WorkedHours> findByMonth(int month);
    List<WorkedHours> findByYear (int year);


    @Modifying
    @Query("DELETE FROM WorkedHours w WHERE w.year = :year")
    void deleteBySemester(@Param("year") int year);

    @Modifying
    @Query("DELETE FROM WorkedHours w WHERE w.month = :month")
    void deleteByMonth(@Param("month") int month);

}

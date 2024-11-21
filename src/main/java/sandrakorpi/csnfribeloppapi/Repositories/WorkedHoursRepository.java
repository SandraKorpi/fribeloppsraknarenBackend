package sandrakorpi.csnfribeloppapi.Repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Models.Semester;
import sandrakorpi.csnfribeloppapi.Models.WorkedHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkedHoursRepository extends JpaRepository<WorkedHours, Long> {
   // List<WorkedHours> findByUser_IdAndYearAndMonthAndDate(long userId, int year, int month, int date);
  Optional<WorkedHours> findByUser_IdAndYearAndMonthAndDate(long userId, int year, int month, int date);

  List<WorkedHours> findByUser_IdAndYearAndMonth(long userId, int year, int month);
  List<WorkedHours> findByYearAndMonth(int year, int month);
    List<WorkedHours> findByUser_Id(Long userId); // Hämta arbetstimmar baserat på userId i User
    Optional<WorkedHours> findByIdAndUser_Id(Long id, Long userId);
    List<WorkedHours> findByUser_IdAndYear(Long userId, int year);
    List<WorkedHours> findByUser_IdAndMonth(Long userId, int month);



    @Modifying
    @Transactional
    @Query("DELETE FROM WorkedHours w WHERE w.year = :year")
    void deleteBySemester(@Param("year") int year);

    @Modifying
    @Transactional
    @Query("DELETE FROM WorkedHours w WHERE w.month = :month")
    void deleteByMonth(@Param("month") int month);

    List<WorkedHours> findAllByUser_IdAndSemester(Long user_id, Semester semester);
}

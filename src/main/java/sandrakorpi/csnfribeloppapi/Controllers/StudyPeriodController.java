package sandrakorpi.csnfribeloppapi.Controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sandrakorpi.csnfribeloppapi.Dtos.StudyPeriodDto;
import sandrakorpi.csnfribeloppapi.Enums.SemesterType;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Services.StudyPeriodService;

/**
 * Antal studieveckor och studietakt för ett kalenderhalvår. Hör alltid till den inloggade användaren.
 */
@RestController
@RequestMapping("/api/study-periods")
public class StudyPeriodController {

    private final StudyPeriodService studyPeriodService;

    public StudyPeriodController(StudyPeriodService studyPeriodService) {
        this.studyPeriodService = studyPeriodService;
    }

    @GetMapping("/{year}/{type}")
    public ResponseEntity<StudyPeriodDto> get(@PathVariable int year,
                                              @PathVariable SemesterType type,
                                              @AuthenticationPrincipal User user) {
        return studyPeriodService.find(user.getId(), year, type)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/{year}/{type}")
    public ResponseEntity<StudyPeriodDto> save(@PathVariable int year,
                                               @PathVariable SemesterType type,
                                               @Valid @RequestBody StudyPeriodDto dto,
                                               @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studyPeriodService.save(user.getId(), year, type, dto));
    }
}

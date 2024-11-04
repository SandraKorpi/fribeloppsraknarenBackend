package sandrakorpi.csnfribeloppapi.Controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sandrakorpi.csnfribeloppapi.Dtos.SemesterDto;
import sandrakorpi.csnfribeloppapi.Models.User;
import sandrakorpi.csnfribeloppapi.Services.SemesterService;

import java.util.List;

@RestController
@RequestMapping("api/semester")
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @PostMapping("/add")
    public ResponseEntity<SemesterDto> addSemester(@RequestBody SemesterDto semesterDto)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body( semesterService.saveSemester(semesterDto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SemesterDto> updateSemester (@PathVariable long id,@RequestBody SemesterDto semesterDto)
    {
       SemesterDto updatedDto = semesterService.updateSemester(id, semesterDto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSemester (@PathVariable long id)
    {
        semesterService.deleteSemester(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<SemesterDto>> getAllSemesters ()
    {
       List<SemesterDto> semesterDtoList = semesterService.findAll();
       return ResponseEntity.ok(semesterDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SemesterDto> findById (@PathVariable long id)
    {
        return ResponseEntity.ok(semesterService.findById(id));
    }
}

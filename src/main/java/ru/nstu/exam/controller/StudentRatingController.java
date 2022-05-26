package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.StudentRatingBean;
import ru.nstu.exam.bean.full.FullStudentRatingBean;
import ru.nstu.exam.bean.student.StudentAnswerBean;
import ru.nstu.exam.security.IsStudent;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.StudentRatingService;

import java.util.List;


@RestController
@RequestMapping("/student-rating")
@RequiredArgsConstructor
@Tag(name = "Student Rating")
public class StudentRatingController {

    private final StudentRatingService studentRatingService;

    @GetMapping
    @Operation(summary = "Get all student ratings")
    public List<StudentRatingBean> findAll() {
        return studentRatingService.findAll();
    }

    @GetMapping("/{id}/full")
    @Operation(summary = "Get full student rating")
    public FullStudentRatingBean getFull(@PathVariable Long id, @RequestParam(required = false, defaultValue = "0") int level) {
        return studentRatingService.findFull(id, level);
    }

    @IsTeacher
    @PutMapping
    @Operation(summary = "Update Student rating", description = "Updates semester rating and moves to Allowed or Not Allowed states")
    public void update(@RequestBody StudentRatingBean studentRatingBean) {
        studentRatingService.update(studentRatingBean);
    }

    @PutMapping("/state")
    @Operation(summary = "Update student rating state")
    public StudentRatingBean updateState(@RequestBody StudentRatingBean bean) {
        return studentRatingService.updateState(bean);
    }

    @IsStudent
    @GetMapping("/{id}/answer")
    @Operation(summary = "Get answers by student rating", description = "Sorted by default by task type (questions first)")
    public List<StudentAnswerBean> getAnswers(
            @PathVariable Long id,
            @PageableDefault(size = 100_000, sort = "task.taskType", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return studentRatingService.getStudentAnswers(id, pageable);
    }
}

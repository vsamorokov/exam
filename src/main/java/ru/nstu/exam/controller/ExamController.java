package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.ExamBean;
import ru.nstu.exam.bean.StudentRatingBean;
import ru.nstu.exam.bean.full.FullExamBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.ExamService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exams")
@Tag(name = "Exam")
public class ExamController {

    private final ExamService examService;

    @IsTeacher
    @GetMapping
    @Operation(summary = "Get all exams")
    public List<ExamBean> getAll() {
        return examService.findAll();
    }

    @GetMapping("/{examId}")
    @Operation(summary = "Get exam by id")
    public ExamBean getOne(@PathVariable Long examId) {
        return examService.findOne(examId);
    }

    @GetMapping("/{examId}/full")
    @Operation(summary = "Get full exam by id")
    public FullExamBean getFull(@PathVariable Long examId, @RequestParam(required = false, defaultValue = "0") int level) {
        return examService.findFull(examId, level);
    }

    @IsTeacher
    @PostMapping
    @Operation(summary = "Create an exam")
    public ExamBean createExam(@RequestBody ExamBean createExamBean, @UserAccount Account account) {
        return examService.createExam(createExamBean, account);
    }

    @IsTeacher
    @PutMapping
    @Operation(summary = "Update an exam")
    public ExamBean updateExam(@RequestBody ExamBean examBean, @UserAccount Account account) {
        return examService.updateExam(examBean, account);
    }

    @IsTeacher
    @PutMapping("/state")
    @Operation(summary = "Update exam state")
    public ExamBean updateState(@RequestBody ExamBean examBean) {
        return examService.updateState(examBean);
    }

    @IsTeacher
    @DeleteMapping("/{examId}")
    @Operation(summary = "Delete an exam")
    public void deleteExam(@PathVariable Long examId) {
        examService.delete(examId);
    }

    @IsTeacher
    @GetMapping("/{examId}/student-ratings")
    @Operation(summary = "Get student ratings by a exam")
    public List<StudentRatingBean> getStudentRatings(@PathVariable Long examId) {
        return examService.findRatings(examId);
    }

    @IsTeacher
    @GetMapping("/{examId}/answers")
    @Operation(summary = "Get answers by a exam")
    public List<AnswerBean> getAnswers(@PathVariable Long examId) {
        return examService.findAnswers(examId);
    }
}

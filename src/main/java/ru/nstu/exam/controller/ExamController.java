package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.*;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.ExamService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @IsTeacher
    @GetMapping("/exams")
    @Operation(summary = "Get all teacher's exams")
    public List<ExamBean> getAll(@UserAccount Account account) {
        return examService.findAll(account);
    }

    @GetMapping("/exams/{examId}")
    @Operation(summary = "Get exam by id")
    public ExamBean getOne(@PathVariable Long examId) {
        return examService.findOne(examId);
    }

    @IsTeacher
    @PostMapping("/exams")
    @Operation(summary = "Create an exam")
    public ExamBean createExam(
            @RequestBody CreateExamBean createExamBean,
            @UserAccount Account account
    ) {
        return examService.createExam(createExamBean, account);
    }

    @IsTeacher
    @PutMapping("/exams/{examId}")
    @Operation(summary = "Update an exam")
    public ExamBean updateExam(
            @PathVariable Long examId,
            @RequestBody CreateExamBean examBean,
            @UserAccount Account account
    ) {
        return examService.updateExam(examId, examBean, account);
    }

    @IsTeacher
    @DeleteMapping("/exams/{examId}")
    @Operation(summary = "Delete an exam")
    public void deleteExam(
            @PathVariable Long examId,
            @UserAccount Account account
    ) {
        examService.deleteExam(examId, account);
    }

    @IsTeacher
    @GetMapping("/exams/{examId}/periods")
    @Operation(summary = "Get exam periods by exam")
    public List<ExamPeriodBean> getPeriods(@PathVariable Long examId) {
        return examService.findPeriods(examId);
    }

    @GetMapping("/periods/{periodId}")
    @Operation(summary = "Get exam period")
    public ExamPeriodBean getPeriod(@PathVariable Long periodId, @UserAccount Account account) {
        return examService.getPeriod(periodId, account);
    }

    @IsTeacher
    @PutMapping("/periods/{periodId}")
    @Operation(summary = "Update exam period (start time or state NOT together)")
    public ExamPeriodBean updatePeriod(@PathVariable Long periodId, @RequestBody UpdateExamPeriodBean examPeriodBean) {
        return examService.updatePeriod(periodId, examPeriodBean);
    }

    @IsTeacher
    @GetMapping("/exams/{examId}/un-passed")
    @Operation(summary = "Get tickets of people who didn't pass an exam")
    public List<TicketBean> getUnPassed(@PathVariable Long examId) {
        return examService.findUnPassed(examId);
    }

    @IsTeacher
    @GetMapping("/periods/{periodId}/ticket")
    @Operation(summary = "Get tickets by a period")
    public List<TicketBean> getTickets(@PathVariable Long periodId) {
        return examService.findTickets(periodId);
    }

    @GetMapping("/periods/{periodId}/messages")
    @Operation(summary = "Get messages by an exam period")
    public Page<MessageBean> getMessages(@PathVariable Long periodId,
                                         @UserAccount Account account,
                                         @PageableDefault Pageable pageable
    ) {
        return examService.findAllMessages(periodId, account, pageable);
    }

    @PostMapping("/periods/{periodId}/messages")
    @Operation(summary = "Send a message to an exam period")
    public MessageBean newMessage(@PathVariable Long periodId,
                                  @RequestBody NewMessageBean messageBean,
                                  @UserAccount Account account
    ) {
        return examService.newMessage(periodId, messageBean, account);
    }
}

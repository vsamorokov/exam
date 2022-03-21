package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.ExamBean;
import ru.nstu.exam.bean.ExamPeriodBean;
import ru.nstu.exam.bean.TicketBean;
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
    @GetMapping("/exam")
    public List<ExamBean> getAll(@UserAccount Account account) {
        return examService.findAll(account);
    }

    @IsTeacher
    @PostMapping("/exam")
    public ExamBean createExam(@RequestBody ExamBean examBean, @UserAccount Account account) {
        return examService.createExam(examBean, account);
    }

    @IsTeacher
    @PutMapping("/exam/{examId}")
    public ExamBean updateExam(@PathVariable Long examId, @RequestBody ExamBean examBean, @UserAccount Account account) {
        return examService.updateExam(examId, examBean, account);
    }

    @IsTeacher
    @GetMapping("/exam/{examId}/period")
    public List<ExamPeriodBean> getPeriods(@PathVariable Long examId) {
        return examService.findPeriods(examId);
    }

    @IsTeacher
    @PutMapping("/exam/{examId}/period/{periodId}")
    public void updatePeriod(@PathVariable Long examId, @PathVariable Long periodId, @RequestBody ExamPeriodBean examPeriodBean) {
        examService.updatePeriod(examId, periodId, examPeriodBean);
    }

    @IsTeacher
    @GetMapping("/exam/{examId}/un-passed")
    public List<TicketBean> getUnPassed(@PathVariable Long examId) {
        return examService.findUnPassed(examId);
    }

    @IsTeacher
    @GetMapping("/period/{periodId}/ticket")
    public List<TicketBean> getTickets(@PathVariable Long periodId) {
        return examService.findTickets(periodId);
    }
}

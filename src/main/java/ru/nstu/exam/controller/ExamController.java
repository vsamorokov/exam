package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.ExamBean;
import ru.nstu.exam.bean.TicketBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.ExamService;

import java.util.List;

@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @IsTeacher
    @GetMapping
    public List<ExamBean> getAll() {
        return examService.findAll();
    }

    @IsTeacher
    @PostMapping
    public void createExam(@RequestBody ExamBean examBean, @UserAccount Account account){
        examService.createExam(examBean, account);
    }

    @IsTeacher
    @GetMapping("/{examId}/un-passed")
    public List<TicketBean> getUnPassed(@PathVariable Long examId) {
        return examService.findUnPassed(examId);
    }

}

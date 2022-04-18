package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.CreateExamRuleBean;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.ExamRuleService;

import java.util.List;


@RestController
@RequestMapping("/exam-rule")
@RequiredArgsConstructor
public class ExamRuleController {

    private final ExamRuleService examRuleService;

    @IsTeacher
    @GetMapping
    @Operation(summary = "Get all exam rules")
    public List<ExamRuleBean> getAll() {
        return examRuleService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one exam rule")
    public ExamRuleBean getOne(@PathVariable Long id) {
        return examRuleService.findOne(id);
    }

    @IsTeacher
    @PostMapping
    @Operation(summary = "Create an exam rule")
    public ExamRuleBean create(@RequestBody CreateExamRuleBean examRuleBean) {
        return examRuleService.createExamRule(examRuleBean);
    }
}

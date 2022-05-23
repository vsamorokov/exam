package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.bean.full.FullExamRuleBean;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.ExamRuleService;

import java.util.List;


@RestController
@RequestMapping("/exam-rule")
@RequiredArgsConstructor
@Tag(name = "Exam rule")
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

    @GetMapping("/{id}/full")
    @Operation(summary = "Get full exam rule")
    public FullExamRuleBean getFull(@PathVariable Long id, @RequestParam(required = false, defaultValue = "0") int level) {
        return examRuleService.findFull(id, level);
    }

    @IsTeacher
    @PostMapping
    @Operation(summary = "Create an exam rule")
    public ExamRuleBean create(@RequestBody ExamRuleBean examRuleBean) {
        return examRuleService.createExamRule(examRuleBean);
    }

    @IsTeacher
    @PutMapping
    @Operation(summary = "Update an exam rule")
    public ExamRuleBean update(@RequestBody ExamRuleBean examRuleBean) {
        return examRuleService.updateExamRule(examRuleBean);
    }
}

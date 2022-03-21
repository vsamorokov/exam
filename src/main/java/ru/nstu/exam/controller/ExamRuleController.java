package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.ExamRuleService;


@RestController
@RequestMapping("/exam-rule")
@RequiredArgsConstructor
public class ExamRuleController {

    private final ExamRuleService examRuleService;

    @IsTeacher
    @PostMapping
    public ExamRuleBean create(@RequestBody ExamRuleBean examRuleBean) {
        return examRuleService.createExamRule(examRuleBean);
    }
}

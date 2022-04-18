package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.DisciplineBean;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.bean.GroupBean;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.DisciplineService;
import ru.nstu.exam.service.ExamRuleService;
import ru.nstu.exam.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/discipline")
@RequiredArgsConstructor
public class DisciplineController {
    private final ExamRuleService examRuleService;
    private final DisciplineService disciplineService;
    private final GroupService groupService;

    @GetMapping
    @Operation(summary = "Get all disciplines")
    public List<DisciplineBean> getAll() {
        return disciplineService.findAll();
    }

    @GetMapping("/{disciplineId}")
    @Operation(summary = "Get one discipline")
    public DisciplineBean getOne(@PathVariable Long disciplineId) {
        return disciplineService.findOne(disciplineId);
    }

    @IsAdmin
    @PostMapping
    @Operation(summary = "Create a discipline")
    public DisciplineBean create(@RequestBody DisciplineBean disciplineBean) {
        return disciplineService.createDiscipline(disciplineBean);
    }

    @IsTeacher
    @GetMapping("/{disciplineId}/exam-rule")
    @Operation(summary = "Get exam rules by discipline")
    public List<ExamRuleBean> findExamRules(@PathVariable Long disciplineId) {
        return examRuleService.findByDiscipline(disciplineId);
    }

    @IsTeacher
    @GetMapping("/{disciplineId}/group")
    @Operation(summary = "Get groups by discipline")
    public List<GroupBean> findGroups(@PathVariable Long disciplineId){
        return groupService.findByDiscipline(disciplineId);
    }
}

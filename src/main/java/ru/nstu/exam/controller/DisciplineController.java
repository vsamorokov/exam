package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.*;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.DisciplineService;
import ru.nstu.exam.service.ExamRuleService;
import ru.nstu.exam.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/discipline")
@RequiredArgsConstructor
@Tag(name = "Disciplines")
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
    public FullDisciplineBean getOne(@PathVariable Long disciplineId, @RequestParam(name = "level", required = false, defaultValue = "0") int level) {
        return disciplineService.findOne(disciplineId, level);
    }

    @IsAdmin
    @PostMapping
    @Operation(summary = "Create a discipline")
    public DisciplineBean create(@RequestBody DisciplineBean disciplineBean) {
        return disciplineService.createDiscipline(disciplineBean);
    }

    @IsAdmin
    @PutMapping("/{disciplineId}")
    @Operation(summary = "Update a discipline")
    public DisciplineBean update(@PathVariable Long disciplineId, @RequestBody DisciplineBean disciplineBean) {
        return disciplineService.update(disciplineId, disciplineBean);
    }

    @IsAdmin
    @DeleteMapping("/{disciplineId}")
    @Operation(summary = "Delete a discipline")
    public void delete(@PathVariable Long disciplineId) {
        disciplineService.delete(disciplineId);
    }

    @GetMapping("/{disciplineId}/themes")
    public List<ThemeBean> getThemes(@PathVariable Long disciplineId) {
        return disciplineService.getThemes(disciplineId);
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

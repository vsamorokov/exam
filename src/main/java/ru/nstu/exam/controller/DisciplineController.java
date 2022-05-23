package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.DisciplineBean;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.bean.ThemeBean;
import ru.nstu.exam.bean.full.FullDisciplineBean;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.DisciplineService;
import ru.nstu.exam.service.ExamRuleService;

import java.util.List;

@RestController
@RequestMapping("/discipline")
@RequiredArgsConstructor
@Tag(name = "Disciplines")
public class DisciplineController {
    private final ExamRuleService examRuleService;
    private final DisciplineService disciplineService;

    @GetMapping
    @Operation(summary = "Get all disciplines")
    public List<DisciplineBean> getAll() {
        return disciplineService.findAll();
    }

    @GetMapping("/{disciplineId}/full")
    @Operation(summary = "Get one discipline")
    public FullDisciplineBean getFull(@PathVariable Long disciplineId, @RequestParam(name = "level", required = false, defaultValue = "0") int level) {
        return disciplineService.findFull(disciplineId, level);
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

    @IsAdmin
    @PutMapping
    @Operation(summary = "Update a discipline")
    public DisciplineBean update(@RequestBody DisciplineBean disciplineBean) {
        return disciplineService.update(disciplineBean);
    }

    @IsAdmin
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a discipline")
    public void delete(@PathVariable Long id) {
        disciplineService.delete(id);
    }

    @GetMapping("/{disciplineId}/themes")
    public List<ThemeBean> getThemes(@PathVariable Long disciplineId) {
        return disciplineService.getThemes(disciplineId);
    }

    @IsTeacher
    @GetMapping("/{id}/exam-rule")
    @Operation(summary = "Get exam rules by discipline")
    public List<ExamRuleBean> findExamRules(@PathVariable Long id) {
        return examRuleService.findByDiscipline(id);
    }
}

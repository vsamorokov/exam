package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.FullThemeBean;
import ru.nstu.exam.bean.TaskBean;
import ru.nstu.exam.bean.ThemeBean;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.ThemeService;

import java.util.List;

@RestController
@RequestMapping("/theme")
@RequiredArgsConstructor
@Tag(name = "Themes")
public class ThemeController {
    private final ThemeService themeService;

    @IsTeacher
    @GetMapping
    @Operation(summary = "Get all themes")
    public List<ThemeBean> getAll() {
        return themeService.findAll();
    }

    @GetMapping("/{themeId}")
    @Operation(summary = "Get one theme")
    public FullThemeBean getOne(@PathVariable Long themeId, @RequestParam(required = false, defaultValue = "0") int level) {
        return themeService.findOne(themeId, level);
    }

    @IsTeacher
    @PostMapping
    @Operation(summary = "Create a theme")
    public ThemeBean create(@RequestBody ThemeBean themeBean) {
        return themeService.createTheme(themeBean);
    }

    @IsTeacher
    @PutMapping("/{themeId}")
    @Operation(summary = "Update a theme")
    public ThemeBean update(@PathVariable Long themeId, @RequestBody ThemeBean themeBean) {
        return themeService.updateTheme(themeId, themeBean);
    }

    @IsTeacher
    @DeleteMapping("/{themeId}")
    @Operation(summary = "Delete a theme")
    public void delete(@PathVariable Long themeId) {
        themeService.delete(themeId);
    }

    @GetMapping("/{themeId}/tasks")
    public List<TaskBean> findTasks(@PathVariable Long themeId) {
        return themeService.findTasks(themeId);
    }
}

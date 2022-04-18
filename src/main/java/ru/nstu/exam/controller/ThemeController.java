package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.ThemeBean;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.ThemeService;

import java.util.List;

@RestController
@RequestMapping("/theme")
@RequiredArgsConstructor
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
    public ThemeBean getOne(@PathVariable Long themeId) {
        return themeService.findOne(themeId);
    }

    @IsTeacher
    @PostMapping
    @Operation(summary = "Create a theme")
    public ThemeBean create(@RequestBody ThemeBean themeBean) {
        return themeService.createTheme(themeBean);
    }

}

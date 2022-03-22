package ru.nstu.exam.controller;

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
    public List<ThemeBean> getAll() {
        return themeService.findAll();
    }

    @IsTeacher
    @PostMapping
    public ThemeBean create(@RequestBody ThemeBean themeBean) {
        return themeService.createTheme(themeBean);
    }

}

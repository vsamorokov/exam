package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nstu.exam.bean.ThemeBean;
import ru.nstu.exam.service.ThemeService;

@RestController
@RequestMapping("/theme")
@RequiredArgsConstructor
public class ThemeController {
    private final ThemeService themeService;

    @PostMapping
    public ThemeBean create(@RequestBody ThemeBean themeBean) {
        return themeService.createTheme(themeBean);
    }

}

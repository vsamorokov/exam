package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.CreateRatingSystemBean;
import ru.nstu.exam.bean.RatingSystemBean;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.RatingSystemService;

import java.util.List;

@RestController
@RequestMapping("/rating-systems")
@RequiredArgsConstructor
public class RatingSystemController {

    private final RatingSystemService ratingSystemService;

    @IsTeacher
    @GetMapping
    @Operation(summary = "Get all rating mappings")
    public List<RatingSystemBean> getAll() {
        return ratingSystemService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one rating mapping")
    public RatingSystemBean getOne(@PathVariable Long id) {
        return ratingSystemService.getOne(id);
    }

    @IsTeacher
    @PostMapping
    @Operation(summary = "Create rating mapping")
    public RatingSystemBean create(@RequestBody CreateRatingSystemBean bean) {
        return ratingSystemService.create(bean);
    }
}

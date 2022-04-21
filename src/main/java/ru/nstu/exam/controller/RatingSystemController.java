package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Rating system")
public class RatingSystemController {

    private final static String EXAMPLE = "{\n" +
            "  \"id\": 0,\n" +
            "  \"name\": \"string\",\n" +
            "  \"ratingMapping\": {\n" +
            "    \"QUESTION\": {\n" +
            "      \"-2\": \"REJECTED\",\n" +
            "      \"0\": \"APPROVED\",\n" +
            "      \"1\": \"APPROVED\",\n" +
            "      \"2\": \"APPROVED\",\n" +
            "    },\n" +
            "    \"EXERCISE\": {\n" +
            "      \"0\": \"REJECTED\",\n" +
            "      \"1\": \"APPROVED\",\n" +
            "      \"2\": \"APPROVED\"\n" +
            "      \"3\": \"APPROVED\"\n" +
            "      \"4\": \"APPROVED\"\n" +
            "      \"5\": \"APPROVED\"\n" +
            "      \"6\": \"APPROVED\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private final RatingSystemService ratingSystemService;

    @IsTeacher
    @GetMapping
    @Operation(summary = "Get all rating mappings",
            responses = @ApiResponse(content = @Content(examples = @ExampleObject(value = EXAMPLE))))
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

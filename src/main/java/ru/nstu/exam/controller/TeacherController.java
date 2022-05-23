package ru.nstu.exam.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nstu.exam.bean.CreateTeacherBean;
import ru.nstu.exam.bean.TeacherBean;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.service.TeacherService;

import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
@Tag(name = "Teacher")
public class TeacherController {

    private final TeacherService teacherService;

    @IsAdmin
    @PostMapping("/bulk")
    @Operation(summary = "Create many teachers")
    public List<TeacherBean> addTeachers(@RequestBody List<CreateTeacherBean> teachers) {
        return teacherService.addTeachers(teachers);
    }

    @IsAdmin
    @PostMapping
    @Operation(summary = "Create one teacher")
    public TeacherBean createTeacher(@RequestBody CreateTeacherBean teacher) {
        return teacherService.createTeacher(teacher);
    }

}

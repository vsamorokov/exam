package ru.nstu.exam.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.DisciplineBean;
import ru.nstu.exam.bean.TeacherBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.TeacherService;

import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @IsAdmin
    @PostMapping("/bulk")
    public List<TeacherBean> addTeachers(@RequestBody List<TeacherBean> teachers) {
        return teacherService.addTeachers(teachers);
    }

    @IsAdmin
    @PostMapping
    public TeacherBean createTeacher(@RequestBody TeacherBean teacher) {
        return teacherService.createTeacher(teacher);
    }

    @IsTeacher
    @GetMapping("/discipline")
    public List<DisciplineBean> getDisciplines(@UserAccount Account account) {
        return teacherService.findDisciplines(account);
    }

    @IsTeacher
    @GetMapping("/me")
    public TeacherBean getSelf(@UserAccount Account account) {
        return teacherService.getSelf(account);
    }

}

package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nstu.exam.bean.StudentBean;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @IsAdmin
    @PostMapping("/bulk")
    public List<StudentBean> addStudents(@RequestBody List<StudentBean> students) {
        return studentService.addStudents(students);
    }

    @IsAdmin
    @PostMapping
    public StudentBean createStudent(@RequestBody StudentBean student) {
        return studentService.createStudent(student);
    }

}

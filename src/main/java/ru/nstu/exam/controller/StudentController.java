package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.StudentBean;
import ru.nstu.exam.bean.student.StudentExamInfoBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.security.IsStudent;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Tag(name = "Student")
public class StudentController {

    private final StudentService studentService;

    @IsAdmin
    @PostMapping("/bulk")
    @Operation(summary = "Create many students")
    public List<StudentBean> addStudents(@RequestBody List<StudentBean> students) {
        return studentService.addStudents(students);
    }

    @IsAdmin
    @PostMapping
    @Operation(summary = "Create one student")
    public StudentBean createStudent(@RequestBody StudentBean student) {
        return studentService.createStudent(student);
    }

    @IsStudent
    @GetMapping("/me")
    @Operation(summary = "Get info about sender student")
    public StudentBean getSelf(@UserAccount Account account) {
        return studentService.findByAccount(account);
    }

    @IsStudent
    @GetMapping("/exam-infos")
    @Operation(summary = "Get student's tickets")
    public List<StudentExamInfoBean> getTickets(@UserAccount Account account) {
        return studentService.getTickets(account);
    }

    @IsTeacher
    @GetMapping("/{studentId}")
    @Operation(summary = "Get student by id")
    public StudentBean getStudent(@PathVariable Long studentId) {
        return studentService.findOne(studentId);
    }
}

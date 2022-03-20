package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.StudentBean;
import ru.nstu.exam.bean.TicketBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.security.IsStudent;
import ru.nstu.exam.security.UserAccount;
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

    @IsStudent
    @GetMapping("/me")
    public StudentBean getSelf(@UserAccount Account account) {
        return studentService.findByAccount(account);
    }

    @IsStudent
    @GetMapping("/ticket")
    public List<TicketBean> getTickets(@UserAccount Account account) {
        return studentService.getTickets(account);
    }
}

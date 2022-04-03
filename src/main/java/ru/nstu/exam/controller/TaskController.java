package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.TaskBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @IsTeacher
    @GetMapping
    public List<TaskBean> findAll(@UserAccount Account account) {
        return taskService.findAll(account);
    }

    @IsTeacher
    @PostMapping
    public TaskBean createTask(@RequestBody TaskBean taskBean) {
        return taskService.createTask(taskBean);
    }
}

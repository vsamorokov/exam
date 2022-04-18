package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Get all tasks")
    public List<TaskBean> findAll(@UserAccount Account account) {
        return taskService.findAll(account);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get one task")
    public TaskBean findOne(@PathVariable Long taskId) {
        return taskService.findOne(taskId);
    }

    @IsTeacher
    @PostMapping
    @Operation(summary = "Create a task")
    public TaskBean createTask(@RequestBody TaskBean taskBean) {
        return taskService.createTask(taskBean);
    }
}

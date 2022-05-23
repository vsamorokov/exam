package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.TaskBean;
import ru.nstu.exam.bean.full.FullTaskBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Tag(name = "Tasks")
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

    @GetMapping("/{taskId}/full")
    @Operation(summary = "Get full task")
    public FullTaskBean findFull(@PathVariable Long taskId, @RequestParam(name = "level", required = false, defaultValue = "0") int level) {
        return taskService.findFull(taskId, level);
    }

    @IsTeacher
    @PostMapping
    @Operation(summary = "Create a task")
    public TaskBean createTask(@RequestBody TaskBean taskBean) {
        return taskService.createTask(taskBean);
    }

    @IsTeacher
    @PutMapping
    @Operation(summary = "Update a task")
    public TaskBean updateTask(@RequestBody TaskBean taskBean) {
        return taskService.update(taskBean);
    }

    @IsTeacher
    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a task (also deletes artefact)")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.delete(taskId);
    }
}

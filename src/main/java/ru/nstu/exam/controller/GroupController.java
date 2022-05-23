package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.GroupBean;
import ru.nstu.exam.bean.StudentBean;
import ru.nstu.exam.bean.full.FullGroupBean;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.service.GroupService;
import ru.nstu.exam.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Tag(name = "Groups")
public class GroupController {
    private final GroupService groupService;
    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Get all groups")
    public List<GroupBean> getAll() {
        return groupService.findAll();
    }

    @GetMapping("/{groupId}")
    @Operation(summary = "Get one group")
    public GroupBean getOne(@PathVariable Long groupId) {
        return groupService.findOne(groupId);
    }

    @GetMapping("/{groupId}/full")
    @Operation(summary = "Get full group")
    public FullGroupBean getFull(
            @PathVariable Long groupId,
            @RequestParam(name = "level", required = false, defaultValue = "0") int level
    ) {
        return groupService.findFull(groupId, level);
    }

    @IsAdmin
    @PostMapping
    @Operation(summary = "Create a group")
    public GroupBean create(@RequestBody GroupBean groupBean) {
        return groupService.createGroup(groupBean);
    }

    @IsAdmin
    @PutMapping
    @Operation(summary = "Edit group")
    public GroupBean edit(@RequestBody GroupBean groupBean) {
        return groupService.editGroup(groupBean);
    }

    @IsAdmin
    @DeleteMapping("/{groupId}")
    @Operation(summary = "Delete group")
    public void delete(@PathVariable Long groupId) {
        groupService.delete(groupId);
    }

    @GetMapping("/{groupId}/students")
    @Operation(summary = "Get students in a group")
    public List<StudentBean> getStudents(@PathVariable Long groupId) {
        return studentService.findByGroup(groupId);
    }
}

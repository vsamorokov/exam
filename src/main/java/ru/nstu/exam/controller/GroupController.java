package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.CreateGroupBean;
import ru.nstu.exam.bean.GroupBean;
import ru.nstu.exam.bean.StudentBean;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.GroupService;
import ru.nstu.exam.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@Tag(name = "Group")
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

    @IsAdmin
    @PostMapping
    @Operation(summary = "Create a group")
    public GroupBean create(@RequestBody CreateGroupBean groupBean) {
        return groupService.createGroup(groupBean);
    }

    @IsAdmin
    @PutMapping("/{groupId}")
    @Operation(summary = "Edit group")
    public GroupBean edit(@PathVariable Long groupId, @RequestBody CreateGroupBean groupBean) {
        return groupService.editGroup(groupId, groupBean);
    }

    @IsTeacher
    @GetMapping("/{groupId}/students")
    @Operation(summary = "Get students in a group")
    public List<StudentBean> getStudents(@PathVariable Long groupId) {
        return studentService.findByGroup(groupId);
    }
}

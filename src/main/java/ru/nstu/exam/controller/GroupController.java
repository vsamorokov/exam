package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.GroupBean;
import ru.nstu.exam.bean.StudentBean;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.service.GroupService;
import ru.nstu.exam.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final StudentService studentService;

    @GetMapping
    public List<GroupBean> getAll() {
        return groupService.findAll();
    }

    @IsAdmin
    @PostMapping
    public GroupBean create(@RequestBody GroupBean groupBean) {
        return groupService.createGroup(groupBean);
    }

    @GetMapping("/{groupId}/student")
    public List<StudentBean> getStudents(@PathVariable Long groupId) {
        return studentService.findByGroup(groupId);
    }
}

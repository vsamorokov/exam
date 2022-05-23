package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.GroupRatingBean;
import ru.nstu.exam.bean.full.FullGroupRatingBean;
import ru.nstu.exam.service.GroupRatingService;

import java.util.List;

@RestController
@RequestMapping("group-ratings")
@RequiredArgsConstructor
@Tag(name = "Group Rating")
public class GroupRatingController {

    private final GroupRatingService groupRatingService;

    @GetMapping
    @Operation(summary = "Get all group ratings")
    public List<GroupRatingBean> findAll() {
        return groupRatingService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one group rating")
    public GroupRatingBean findOne(@PathVariable Long id) {
        return groupRatingService.findOne(id);
    }

    @GetMapping("/{id}/full")
    @Operation(summary = "Get full group rating")
    public FullGroupRatingBean findFull(@PathVariable Long id,
                                        @RequestParam(name = "level", required = false, defaultValue = "0") int level) {
        return groupRatingService.findFull(id, level);
    }

    @PostMapping
    @Operation(summary = "Create group rating")
    public GroupRatingBean create(@RequestBody GroupRatingBean bean) {
        return groupRatingService.create(bean);
    }

    @PutMapping
    @Operation(summary = "Update group rating")
    public GroupRatingBean update(@RequestBody GroupRatingBean bean) {
        return groupRatingService.update(bean);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete group rating")
    public void delete(@PathVariable Long id) {
        groupRatingService.delete(id);
    }
}

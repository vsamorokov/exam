package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.FullTicketBean;
import ru.nstu.exam.bean.StudentAnswerBean;
import ru.nstu.exam.bean.UpdateTicketBean;
import ru.nstu.exam.security.IsStudent;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.TicketService;

import java.util.List;


@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
@Tag(name = "Ticket")
public class TicketController {

    private final TicketService ticketService;

    @IsTeacher
    @PutMapping("/rating")
    @Operation(summary = "Update rating")
    public void updateTicketsRating(@RequestBody List<UpdateTicketBean> ticketBeans) {
        ticketService.update(ticketBeans);
    }

    @IsStudent
    @GetMapping("/{ticketId}/answer")
    @Operation(summary = "Get answers by ticket", description = "Sorted by default by task type (questions first)")
    public List<StudentAnswerBean> getAnswers(
            @PathVariable Long ticketId,
            @PageableDefault(size = 100_000, sort = "task.taskType", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ticketService.getAnswers(ticketId, pageable);
    }

    @GetMapping("/{ticketId}/full")
    @Operation(summary = "Get full ticket")
    public FullTicketBean getFull(@PathVariable Long ticketId, @RequestParam(required = false, defaultValue = "0") int level) {
        return ticketService.findFull(ticketId, level);
    }


}

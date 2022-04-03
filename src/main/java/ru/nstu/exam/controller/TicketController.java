package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.TicketBean;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.service.TicketService;

import java.util.List;


@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @IsTeacher
    @PostMapping("/rating")
    public void updateTicketsRating(@RequestBody List<TicketBean> ticketBeans) {
        ticketService.update(ticketBeans);
    }

    @GetMapping("/{ticketId}/answer")
    public List<AnswerBean> getAnswers(@PathVariable Long ticketId) {
        return ticketService.getAnswers(ticketId);
    }


}

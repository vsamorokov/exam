package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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


}

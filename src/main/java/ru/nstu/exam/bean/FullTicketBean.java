package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullTicketBean {
    private TicketBean ticket;
    private List<FullAnswerBean> answers;
    private FullStudentBean student;
}

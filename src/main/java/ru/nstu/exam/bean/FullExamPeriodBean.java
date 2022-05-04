package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullExamPeriodBean {
    private ExamPeriodBean examPeriod;
    private List<FullTicketBean> tickets;
}

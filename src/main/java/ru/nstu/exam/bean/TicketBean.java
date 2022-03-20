package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketBean extends EntityBean {

    private Integer semesterRating;

    private Integer examRating;

    private Boolean allowed;

    private ExamPeriodBean examPeriod;

    private StudentBean student;
}

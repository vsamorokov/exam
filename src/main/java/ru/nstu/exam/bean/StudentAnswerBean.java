package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.enums.AnswerStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentAnswerBean extends EntityBean {

    private StudentTaskBean task;

    private Integer rating;

    private Long ticketId;

    private AnswerStatus status;

    private Integer number;
}

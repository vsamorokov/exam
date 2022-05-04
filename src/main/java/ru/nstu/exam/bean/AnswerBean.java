package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.enums.AnswerStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerBean extends EntityBean {

    private Long taskId;

    private Integer rating;

    private Long ticketId;

    private Integer number; // index of an answer in ticket

    private AnswerStatus status;
}

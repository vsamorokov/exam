package ru.nstu.exam.bean.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.bean.EntityBean;
import ru.nstu.exam.enums.AnswerState;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentAnswerBean extends EntityBean {

    private StudentTaskBean task;

    private Integer rating;

    private Long studentRatingId;

    private AnswerState state;

    private Integer number;
}

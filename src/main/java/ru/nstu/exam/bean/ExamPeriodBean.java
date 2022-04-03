package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.enums.ExamPeriodState;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamPeriodBean extends EntityBean {

    private Long start;

    private Long end;

    private ExamBean exam;

    private ExamPeriodState state;
}

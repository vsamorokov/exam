package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.enums.ExamPeriodState;

import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamPeriodBean extends EntityBean {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime start;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime end;

    private ExamBean exam;

    private ExamPeriodState state;
}

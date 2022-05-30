package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.enums.ExamState;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamBean extends EntityBean {

    private String name = "";

    private Long disciplineId;

    private Long groupId;

    private boolean oneGroup;

    private Long start;

    private Long end;

    private ExamState state;

    private Long teacherId;
}

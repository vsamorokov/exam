package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentTicketBean extends EntityBean {
    private String disciplineName;

    private Integer semesterRating;

    private Integer examRating;

    private Boolean allowed;

    private ExamPeriodBean examPeriod;

    private TeacherBean teacher;

    private Integer maxQuestionRating;

    private Integer maxExerciseRating;
}

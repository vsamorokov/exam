package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.enums.StudentRatingState;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentRatingBean extends EntityBean {
    private Integer semesterRating;

    private Integer questionRating;

    private Integer exerciseRating;

    private Long examId;

    private Long studentId;

    private Long groupRatingId;

    private StudentRatingState studentRatingState;
}

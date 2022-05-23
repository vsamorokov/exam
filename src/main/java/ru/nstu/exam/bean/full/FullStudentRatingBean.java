package ru.nstu.exam.bean.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nstu.exam.bean.StudentRatingBean;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullStudentRatingBean {
    private StudentRatingBean studentRating;
    private List<FullAnswerBean> answers;
    private FullStudentBean student;
    private FullExamBean exam;
    private FullGroupRatingBean groupRating;
}

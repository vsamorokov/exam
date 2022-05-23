package ru.nstu.exam.bean.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nstu.exam.bean.ExamBean;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullExamBean {
    private ExamBean exam;
    private FullGroupBean group;
    private List<FullStudentRatingBean> tickets;
}

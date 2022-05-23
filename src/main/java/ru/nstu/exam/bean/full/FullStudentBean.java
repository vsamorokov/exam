package ru.nstu.exam.bean.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nstu.exam.bean.StudentBean;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullStudentBean {
    private StudentBean student;
    private FullGroupBean group;
    private List<FullStudentRatingBean> studentRatings;
}

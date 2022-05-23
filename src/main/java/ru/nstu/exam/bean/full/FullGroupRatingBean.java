package ru.nstu.exam.bean.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nstu.exam.bean.GroupRatingBean;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullGroupRatingBean {
    private GroupRatingBean groupRating;
    private List<FullStudentRatingBean> studentRatings;
    private FullDisciplineBean discipline;
    private FullGroupBean group;

}

package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.enums.AnswerStatus;
import ru.nstu.exam.enums.TaskType;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingSystemBean extends EntityBean {

    private String name;

    private Map<TaskType, Map<Integer, AnswerStatus>> ratingMapping;
}

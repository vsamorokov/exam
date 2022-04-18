package ru.nstu.exam.entity.utils;

import lombok.Builder;
import lombok.Data;
import ru.nstu.exam.enums.AnswerStatus;
import ru.nstu.exam.enums.TaskType;

@Data
@Builder
public class RatingMapping {
    private TaskType taskType;
    private AnswerStatus status;
    private Integer rating;
}

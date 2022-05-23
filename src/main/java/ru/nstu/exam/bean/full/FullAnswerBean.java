package ru.nstu.exam.bean.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nstu.exam.bean.AnswerBean;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullAnswerBean {
    private AnswerBean answer;
    private FullTaskBean task;
    private List<FullMessageBean> messages;
}

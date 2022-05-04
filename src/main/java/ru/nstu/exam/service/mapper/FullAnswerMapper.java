package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.FullAnswerBean;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.entity.Message;
import ru.nstu.exam.entity.Task;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullAnswerMapper implements Mapper<FullAnswerBean, Answer> {

    private final FullTaskMapper taskMapper;
    private final FullMessageMapper messageMapper;

    @Override
    public FullAnswerBean map(Answer entity, int level) {
        FullAnswerBean fullAnswerBean = new FullAnswerBean();
        if (level >= 0) {
            AnswerBean answerBean = new AnswerBean();
            answerBean.setId(entity.getId());
            answerBean.setRating(entity.getRating());
            answerBean.setNumber(entity.getNumber());
            answerBean.setTicketId(entity.getTicket() == null ? null : entity.getTicket().getId());
            answerBean.setStatus(entity.getStatus());
            answerBean.setTaskId(entity.getTask() == null ? null : entity.getTask().getId());
            fullAnswerBean.setAnswer(answerBean);
        }
        if (level >= 1) {
            Task task = entity.getTask();
            if (task != null) {
                fullAnswerBean.setTask(taskMapper.map(task, level - 1));
            }
            List<Message> messages = entity.getMessages();
            fullAnswerBean.setMessages(
                    CollectionUtils.emptyIfNull(messages).stream()
                            .map(m -> messageMapper.map(m, level - 1))
                            .collect(Collectors.toList())
            );
        }
        return fullAnswerBean;
    }
}

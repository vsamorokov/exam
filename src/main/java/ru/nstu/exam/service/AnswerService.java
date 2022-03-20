package ru.nstu.exam.service;

import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.entity.Theme;
import ru.nstu.exam.entity.Ticket;
import ru.nstu.exam.repository.AnswerRepository;

import java.util.List;

@Service
public class AnswerService extends BasePersistentService<Answer, AnswerBean, AnswerRepository> {

    public AnswerService(AnswerRepository repository) {
        super(repository);
    }

    public void generateAnswers(Ticket ticket, List<Theme> themes) {

    }

    @Override
    protected AnswerBean map(Answer entity) {
        return null;
    }

    @Override
    protected Answer map(AnswerBean bean) {
        return null;
    }
}

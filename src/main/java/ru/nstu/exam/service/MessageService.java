package ru.nstu.exam.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.MessageBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.entity.ExamPeriod;
import ru.nstu.exam.entity.Message;
import ru.nstu.exam.repository.MessageRepository;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;
import static ru.nstu.exam.utils.Utils.toMillis;

@Service
public class MessageService extends BasePersistentService<Message, MessageBean, MessageRepository> {

    private final AccountService accountService;

    public MessageService(MessageRepository repository, AccountService accountService) {
        super(repository);
        this.accountService = accountService;
    }

    public Page<MessageBean> findAllByAnswer(Answer answer, Pageable pageable) {
        return getRepository().findAllByAnswer(answer, pageable).map(this::map);
    }

    public Page<MessageBean> findAllByExamPeriod(ExamPeriod examPeriod, Pageable pageable) {
        return getRepository().findAllByExamPeriodAndAnswerIsNull(examPeriod, pageable).map(this::map);
    }

    public MessageBean createAnswerMessage(MessageBean messageBean, Answer answer, Account account) {
        Message message = map(messageBean);
        message.setSendTime(LocalDateTime.now(UTC));
        message.setAnswer(answer);
        message.setAccount(account);
        return map(save(message));
    }

    public MessageBean createExamPeriodMessage(MessageBean messageBean, ExamPeriod examPeriod, Account account) {
        Message message = map(messageBean);
        message.setSendTime(LocalDateTime.now(UTC));
        message.setExamPeriod(examPeriod);
        message.setAccount(account);
        return map(save(message));
    }

    @Override
    protected MessageBean map(Message entity) {
        MessageBean messageBean = new MessageBean();
        messageBean.setAccount(accountService.map(entity.getAccount()));
        messageBean.setText(entity.getText());
        messageBean.setSendTime(toMillis(entity.getSendTime()));
        return messageBean;
    }

    @Override
    protected Message map(MessageBean bean) {
        Message message = new Message();
        message.setText(bean.getText());
        return message;
    }
}

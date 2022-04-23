package ru.nstu.exam.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.nstu.exam.bean.MessageBean;
import ru.nstu.exam.bean.NewMessageBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.repository.MessageRepository;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;
import static ru.nstu.exam.exception.ExamException.userError;
import static ru.nstu.exam.utils.Utils.toMillis;

@Service
public class MessageService extends BasePersistentService<Message, MessageBean, MessageRepository> {

    private final ArtefactService artefactService;

    public MessageService(MessageRepository repository, ArtefactService artefactService) {
        super(repository);
        this.artefactService = artefactService;
    }

    public Page<MessageBean> findAllByAnswer(Answer answer, Pageable pageable) {
        return getRepository().findAllByAnswer(answer, pageable).map(this::map);
    }

    public Page<MessageBean> findAllByExamPeriod(ExamPeriod examPeriod, Pageable pageable) {
        return getRepository().findAllByExamPeriodAndAnswerIsNull(examPeriod, pageable).map(this::map);
    }

    public MessageBean createAnswerMessage(NewMessageBean messageBean, Answer answer, Account account) {
        Message message = checkNewMessage(messageBean);
        message.setAnswer(answer);
        message.setAccount(account);
        return map(save(message));
    }

    public MessageBean createExamPeriodMessage(NewMessageBean messageBean, ExamPeriod examPeriod, Account account) {
        Message message = checkNewMessage(messageBean);

        message.setExamPeriod(examPeriod);
        message.setAccount(account);
        return map(save(message));
    }

    private Message checkNewMessage(NewMessageBean messageBean) {
        if (messageBean.getArtefactId() == null && !StringUtils.hasText(messageBean.getText())) {
            userError("Empty message");
        }

        Message message = new Message();
        if (messageBean.getArtefactId() != null) {
            Artefact artefact = artefactService.getArtefact(messageBean.getArtefactId());
            if (artefact == null) {
                userError("Artefact not found");
            }
            message.setArtefact(artefact);
        }
        message.setText(messageBean.getText());
        message.setSendTime(LocalDateTime.now(UTC));
        return message;
    }

    @Override
    protected MessageBean map(Message entity) {
        MessageBean messageBean = new MessageBean();
        messageBean.setId(entity.getId());
        messageBean.setAccountId(entity.getAccount().getId());
        messageBean.setText(entity.getText());
        messageBean.setSendTime(toMillis(entity.getSendTime()));
        if (entity.getArtefact() != null) {
            messageBean.setArtefactId(entity.getArtefact().getId());
        }
        return messageBean;
    }

    @Override
    protected Message map(MessageBean bean) {
        Message message = new Message();
        message.setText(bean.getText());
        return message;
    }
}

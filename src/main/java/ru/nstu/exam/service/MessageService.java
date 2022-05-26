package ru.nstu.exam.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.nstu.exam.bean.MessageBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.entity.Artefact;
import ru.nstu.exam.entity.Message;
import ru.nstu.exam.repository.MessageRepository;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;
import static ru.nstu.exam.exception.ExamException.userError;
import static ru.nstu.exam.utils.Utils.checkNotNull;
import static ru.nstu.exam.utils.Utils.toMillis;

@Service
public class MessageService extends BasePersistentService<Message, MessageBean, MessageRepository> {

    private final ArtefactService artefactService;
    private final NotificationService notificationService;

    public MessageService(MessageRepository repository, ArtefactService artefactService, NotificationService notificationService) {
        super(repository);
        this.artefactService = artefactService;
        this.notificationService = notificationService;
    }

    public Page<MessageBean> findAllByAnswer(Answer answer, Pageable pageable) {
        return getRepository().findAllByAnswer(answer, pageable).map(this::map);
    }

    public MessageBean createAnswerMessage(MessageBean messageBean, Answer answer, Account account) {
        Message message = checkNewMessage(messageBean);
        message.setAnswer(answer);
        message.setAccount(account);
        Message saved = save(message);
        notificationService.newMessage(saved);
        return map(saved);
    }

    private Message checkNewMessage(MessageBean messageBean) {
        if (messageBean.getArtefactId() == null && !StringUtils.hasText(messageBean.getText())) {
            userError("Empty message");
        }

        Message message = new Message();
        if (messageBean.getArtefactId() != null) {
            Artefact artefact = artefactService.getArtefact(messageBean.getArtefactId());
            checkNotNull(artefact, "Artefact not found");
            message.setArtefact(artefact);
        }
        message.setText(messageBean.getText());
        message.setSendTime(LocalDateTime.now(UTC));
        return message;
    }

    @Override
    public void delete(Message message) {
        if (message.getArtefact() != null) {
            artefactService.delete(message.getArtefact());
        }
        super.delete(message);
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

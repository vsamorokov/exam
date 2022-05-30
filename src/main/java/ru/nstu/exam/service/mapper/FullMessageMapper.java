package ru.nstu.exam.service.mapper;

import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.AccountBean;
import ru.nstu.exam.bean.ArtefactBean;
import ru.nstu.exam.bean.MessageBean;
import ru.nstu.exam.bean.full.FullMessageBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.entity.Artefact;
import ru.nstu.exam.entity.Message;

import static ru.nstu.exam.utils.Utils.toMillis;

@Component
public class FullMessageMapper implements Mapper<FullMessageBean, Message> {

    @Override
    public FullMessageBean map(Message entity, int level) {
        FullMessageBean fullMessageBean = new FullMessageBean();
        if (level >= 0) {
            MessageBean messageBean = new MessageBean();
            messageBean.setId(entity.getId());
            messageBean.setText(entity.getText());
            messageBean.setArtefactId(entity.getArtefact() == null ? null : entity.getArtefact().getId());
            messageBean.setSendTime(toMillis(entity.getSendTime()));
            messageBean.setAccountId(entity.getAccount() == null ? null : entity.getAccount().getId());
            fullMessageBean.setMessage(messageBean);

        }
        if (level >= 1) {
            Account account = entity.getAccount();
            if (account != null) {
                AccountBean accountBean = new AccountBean();
                accountBean.setId(account.getId());
                accountBean.setUsername(account.getUsername());
                accountBean.setName(account.getName());
                accountBean.setSurname(account.getSurname());
                accountBean.setRoles(account.getRoles());
                fullMessageBean.setAccount(accountBean);
            }

            Artefact artefact = entity.getArtefact();
            if (artefact != null) {
                ArtefactBean artefactBean = new ArtefactBean();
                artefactBean.setId(artefact.getId());
                artefactBean.setFileName(artefact.getFileName());
                artefactBean.setArtefactType(artefact.getArtefactType());
                artefactBean.setFileSize(artefactBean.getFileSize());
                fullMessageBean.setArtefact(artefactBean);
            }
        }
        return fullMessageBean;
    }
}

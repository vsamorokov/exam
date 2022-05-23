package ru.nstu.exam.service.mapper;

import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.ArtefactBean;
import ru.nstu.exam.bean.TaskBean;
import ru.nstu.exam.bean.full.FullTaskBean;
import ru.nstu.exam.entity.Artefact;
import ru.nstu.exam.entity.Task;

@Component
public class FullTaskMapper implements Mapper<FullTaskBean, Task> {

    @Override
    public FullTaskBean map(Task entity, int level) {
        FullTaskBean bean = new FullTaskBean();
        TaskBean taskBean = new TaskBean();
        if (level >= 0) {
            taskBean.setId(entity.getId());
            taskBean.setText(entity.getText());
            taskBean.setTaskType(entity.getTaskType());
            if (entity.getTheme() != null) {
                taskBean.setThemeId(entity.getTheme().getId());
            }
            bean.setTask(taskBean);
            Artefact artefact = entity.getArtefact();
            if (artefact != null) {
                ArtefactBean artefactBean = new ArtefactBean();
                artefactBean.setId(artefact.getId());
                artefactBean.setArtefactType(artefact.getArtefactType());
                artefactBean.setFileName(artefact.getFileName());
                artefactBean.setFileSize(artefact.getFileSize());
                bean.setArtefact(artefactBean);
            }
        }
        return bean;
    }
}

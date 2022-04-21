package ru.nstu.exam.service.mapper;

import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.ArtefactBean;
import ru.nstu.exam.bean.FullTaskBean;
import ru.nstu.exam.entity.Artefact;
import ru.nstu.exam.entity.Task;

@Component
public class FullTaskMapper implements Mapper<FullTaskBean, Task> {

    @Override
    public FullTaskBean map(Task entity, int level) {
        FullTaskBean taskBean = new FullTaskBean();
        if (level >= 0) {
            taskBean.setId(entity.getId());
            taskBean.setText(entity.getText());
            taskBean.setTaskType(entity.getTaskType());
            taskBean.setThemeId(entity.getTheme().getId());

            Artefact artefact = entity.getArtefact();
            ArtefactBean artefactBean = new ArtefactBean();
            artefactBean.setId(artefact.getId());
            artefactBean.setArtefactType(artefact.getArtefactType());
            artefactBean.setFileName(artefact.getFileName());
            artefactBean.setFileSize(artefact.getFileSize());
            taskBean.setArtefact(artefactBean);
        }
        return taskBean;
    }
}

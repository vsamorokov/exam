package ru.nstu.exam.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.nstu.exam.bean.FullTaskBean;
import ru.nstu.exam.bean.TaskBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.enums.TaskType;
import ru.nstu.exam.repository.TaskRepository;
import ru.nstu.exam.service.mapper.FullTaskMapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class TaskService extends BasePersistentService<Task, TaskBean, TaskRepository> {

    private final ThemeService themeService;
    private final TeacherService teacherService;
    private final ArtefactService artefactService;
    private final FullTaskMapper taskMapper;

    public TaskService(TaskRepository repository, ThemeService themeService, TeacherService teacherService, ArtefactService artefactService, FullTaskMapper taskMapper) {
        super(repository);
        this.themeService = themeService;
        this.teacherService = teacherService;
        this.artefactService = artefactService;
        this.taskMapper = taskMapper;
    }

    public List<TaskBean> findAll(Account account) {
        Teacher teacher = teacherService.findByAccount(account);
        if (teacher == null) {
            userError("Teacher not found");
        }
        Set<Theme> themes = teacher.getDisciplines().stream().flatMap(d -> d.getThemes().stream()).collect(Collectors.toSet());
        return mapToBeans(getRepository().findAllByThemeIn(themes));
    }

    public FullTaskBean findOne(Long taskId, int level) {
        Task task = findById(taskId);
        if (task == null) {
            userError("Task not found");
        }
        return taskMapper.map(task, level);
    }

    public TaskBean createTask(TaskBean taskBean) {
        Theme theme = themeService.findById(taskBean.getThemeId());
        if (theme == null) {
            userError("No theme with specified id");
        }
        if (!StringUtils.hasText(taskBean.getText()) && taskBean.getArtefactId() == null) {
            userError("No content provided");
        }

        Task task = map(taskBean);
        task.setTheme(theme);

        if (taskBean.getArtefactId() != null) {
            Artefact artefact = artefactService.getArtefact(taskBean.getArtefactId());
            if (artefact == null) {
                userError("Artefact not found");
            }
            task.setArtefact(artefact);
        }

        return map(save(task));
    }

    public TaskBean update(Long id, TaskBean taskBean) {
        if (!StringUtils.hasText(taskBean.getText()) && taskBean.getArtefactId() == null) {
            userError("No content provided");
        }
        Task task = findById(id);
        if (task == null) {
            userError("Task not found");
        }
        task.setText(taskBean.getText());
        task.setTaskType(taskBean.getTaskType());

        if (!Objects.equals(task.getTheme().getId(), taskBean.getThemeId())) {
            Theme theme = themeService.findById(taskBean.getThemeId());
            if (theme == null) {
                userError("No theme with specified id");
            }
            task.setTheme(theme);
        }

        Long artefactId = task.getArtefact() == null ? null : task.getArtefact().getId();
        if (!Objects.equals(artefactId, taskBean.getArtefactId())) {
            Artefact artefact = artefactService.getArtefact(taskBean.getArtefactId());
            if (artefact == null) {
                userError("Artefact not found");
            }
            task.setArtefact(artefact);
        }

        return map(save(task));
    }

    public void delete(Long id) {
        Task task = findById(id);
        if (task == null) {
            userError("Task not found");
        }
        if (task.getArtefact() != null) {
            artefactService.delete(task.getArtefact());
        }
        delete(task);
    }

    public List<Task> getQuestions(ExamRule examRule) {
        return getRepository().findAllByThemeInAndTaskType(examRule.getThemes(), TaskType.QUESTION);
    }

    public List<Task> getExercises(ExamRule examRule) {
        return getRepository().findAllByThemeInAndTaskType(examRule.getThemes(), TaskType.EXERCISE);
    }

    @Override
    protected TaskBean map(Task entity) {
        TaskBean taskBean = new TaskBean();
        taskBean.setId(entity.getId());
        taskBean.setText(entity.getText());
        taskBean.setTaskType(entity.getTaskType());
        taskBean.setThemeId(entity.getTheme().getId());
        taskBean.setArtefactId(entity.getArtefact().getId());
        return taskBean;
    }

    @Override
    protected Task map(TaskBean bean) {
        Task task = new Task();
        task.setText(bean.getText());
        task.setTaskType(bean.getTaskType());
        return task;
    }

}

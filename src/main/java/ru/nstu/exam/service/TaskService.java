package ru.nstu.exam.service;

import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.TaskBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.enums.TaskType;
import ru.nstu.exam.repository.TaskRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class TaskService extends BasePersistentService<Task, TaskBean, TaskRepository> {

    private final ThemeService themeService;
    private final TeacherService teacherService;
    private final ArtefactService artefactService;

    public TaskService(TaskRepository repository, ThemeService themeService, TeacherService teacherService, ArtefactService artefactService) {
        super(repository);
        this.themeService = themeService;
        this.teacherService = teacherService;
        this.artefactService = artefactService;
    }

    public List<TaskBean> findAll(Account account) {
        Teacher teacher = teacherService.findByAccount(account);
        if (teacher == null) {
            userError("Teacher not found");
        }
        Set<Theme> themes = teacher.getDisciplines().stream().flatMap(d -> d.getThemes().stream()).collect(Collectors.toSet());
        return mapToBeans(getRepository().findAllByThemeIn(themes));
    }

    public TaskBean findOne(Long taskId) {
        Task task = findById(taskId);
        if (task == null) {
            userError("Task not found");
        }
        return map(task);
    }

    public TaskBean createTask(TaskBean taskBean) {
        Theme theme = themeService.findById(taskBean.getThemeId());
        if (theme == null) {
            userError("No theme with specified id");
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

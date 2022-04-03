package ru.nstu.exam.service;

import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.TaskBean;
import ru.nstu.exam.bean.ThemeBean;
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

    public TaskService(TaskRepository repository, ThemeService themeService, TeacherService teacherService) {
        super(repository);
        this.themeService = themeService;
        this.teacherService = teacherService;
    }

    public TaskBean createTask(TaskBean taskBean){
        ThemeBean themeBean = taskBean.getTheme();
        if(themeBean == null) {
            userError("Task must have a theme");
        }
        if(themeBean.getId() == null){
            userError("Theme must have id");
        }
        Theme theme = themeService.findById(themeBean.getId());
        if (theme == null) {
            userError("No theme with specified id");
        }

        Task task = map(taskBean);
        task.setTheme(theme);

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
        taskBean.setCost(entity.getCost());
        taskBean.setText(entity.getText());
        taskBean.setTaskType(entity.getTaskType());
        taskBean.setTheme(themeService.map(entity.getTheme()));
        return taskBean;
    }

    @Override
    protected Task map(TaskBean bean) {
        Task task = new Task();
        task.setCost(bean.getCost());
        task.setText(bean.getText());
        task.setTaskType(bean.getTaskType());
        return task;
    }

    public List<TaskBean> findAll(Account account) {
        Teacher teacher = teacherService.findByAccount(account);
        if (teacher == null) {
            userError("Teacher not found");
        }
        Set<Theme> themes = teacher.getDisciplines().stream().flatMap(d -> d.getThemes().stream()).collect(Collectors.toSet());
        return mapToBeans(getRepository().findAllByThemeIn(themes));
    }
}

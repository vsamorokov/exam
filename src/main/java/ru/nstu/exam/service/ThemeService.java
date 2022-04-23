package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.nstu.exam.bean.FullThemeBean;
import ru.nstu.exam.bean.TaskBean;
import ru.nstu.exam.bean.ThemeBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Task;
import ru.nstu.exam.entity.Theme;
import ru.nstu.exam.repository.ThemeRepository;
import ru.nstu.exam.service.mapper.FullThemeMapper;

import java.util.List;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class ThemeService extends BasePersistentService<Theme, ThemeBean, ThemeRepository> {

    private final DisciplineService disciplineService;
    private final TaskService taskService;
    private final FullThemeMapper themeMapper;

    public ThemeService(ThemeRepository repository, DisciplineService disciplineService, @Lazy TaskService taskService, FullThemeMapper themeMapper) {
        super(repository);
        this.disciplineService = disciplineService;
        this.taskService = taskService;
        this.themeMapper = themeMapper;
    }

    public FullThemeBean findOne(Long themeId, int level) {
        Theme theme = findById(themeId);
        if (theme == null) {
            userError("Theme not found");
        }
        return themeMapper.map(theme, level);
    }

    public ThemeBean createTheme(ThemeBean themeBean) {

        Discipline discipline = disciplineService.findById(themeBean.getDisciplineId());
        if (discipline == null) {
            userError("Discipline with specified id cannot be found");
        }
        if (!StringUtils.hasText(themeBean.getName())) {
            userError("Theme must have name");
        }

        Theme theme = map(themeBean);
        theme.setDiscipline(discipline);

        return map(save(theme));
    }

    public ThemeBean updateTheme(Long themeId, ThemeBean themeBean) {
        Theme theme = findById(themeId);
        if (theme == null) {
            userError("Theme not found");
        }

        Discipline discipline = disciplineService.findById(themeBean.getDisciplineId());
        if (discipline == null) {
            userError("Discipline with specified id cannot be found");
        }
        if (!StringUtils.hasText(themeBean.getName())) {
            userError("Theme must have name");
        }
        theme.setName(themeBean.getName());
        theme.setDiscipline(discipline);
        return map(save(theme));
    }

    public void delete(Long id) {
        Theme theme = findById(id);
        if (theme == null) {
            userError("Theme not found");
        }
        for (Task task : CollectionUtils.emptyIfNull(theme.getTasks())) {
            taskService.delete(task.getId());
        }
        delete(theme);
    }

    public List<TaskBean> findTasks(Long themeId) {
        Theme theme = findById(themeId);
        if (theme == null) {
            userError("Theme not found");
        }
        return taskService.mapToBeans(theme.getTasks());
    }

    @Override
    protected ThemeBean map(Theme entity) {
        ThemeBean themeBean = new ThemeBean();
        themeBean.setId(entity.getId());
        themeBean.setName(entity.getName());
        if (entity.getDiscipline() != null) {
            themeBean.setDisciplineId(entity.getDiscipline().getId());
        }
        return themeBean;
    }

    @Override
    protected Theme map(ThemeBean bean) {
        Theme theme = new Theme();
        theme.setName(bean.getName());
        return theme;
    }

}

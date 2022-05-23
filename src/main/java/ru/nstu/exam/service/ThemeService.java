package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.TaskBean;
import ru.nstu.exam.bean.ThemeBean;
import ru.nstu.exam.bean.full.FullThemeBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.ExamRule;
import ru.nstu.exam.entity.Task;
import ru.nstu.exam.entity.Theme;
import ru.nstu.exam.repository.ThemeRepository;
import ru.nstu.exam.service.mapper.FullThemeMapper;

import java.util.Collection;
import java.util.List;

import static ru.nstu.exam.utils.Utils.checkNotEmpty;
import static ru.nstu.exam.utils.Utils.checkNotNull;

@Service
public class ThemeService extends BasePersistentService<Theme, ThemeBean, ThemeRepository> {

    private final DisciplineService disciplineService;
    private final TaskService taskService;
    private final ExamRuleService examRuleService;
    private final FullThemeMapper themeMapper;

    public ThemeService(ThemeRepository repository, DisciplineService disciplineService, @Lazy TaskService taskService, @Lazy ExamRuleService examRuleService, FullThemeMapper themeMapper) {
        super(repository);
        this.disciplineService = disciplineService;
        this.taskService = taskService;
        this.examRuleService = examRuleService;
        this.themeMapper = themeMapper;
    }

    public ThemeBean findOne(Long id) {
        Theme theme = findById(id);
        checkNotNull(theme, String.format("Theme with id %s not found", id));
        return map(theme);
    }

    public FullThemeBean findFull(Long themeId, int level) {
        Theme theme = findById(themeId);
        checkNotNull(theme, String.format("Theme with id %s not found", themeId));
        return themeMapper.map(theme, level);
    }

    public ThemeBean createTheme(ThemeBean themeBean) {
        Discipline discipline = disciplineService.findById(themeBean.getDisciplineId());
        checkNotNull(discipline, "Discipline with id" + themeBean.getDisciplineId() + " not be found");
        checkNotEmpty(themeBean.getName(), "Theme must have name");

        Theme theme = map(themeBean);
        theme.setDiscipline(discipline);

        return map(save(theme));
    }

    public ThemeBean updateTheme(ThemeBean themeBean) {
        Theme theme = findById(themeBean.getId());
        checkNotNull(theme, String.format("Theme with id %s not found", themeBean.getId()));

        Discipline discipline = disciplineService.findById(themeBean.getDisciplineId());
        checkNotNull(discipline, "Discipline with id" + themeBean.getDisciplineId() + " not be found");
        checkNotEmpty(themeBean.getName(), "Theme must have name");

        theme.setName(themeBean.getName());
        theme.setDiscipline(discipline);
        return map(save(theme));
    }

    public void delete(Long id) {
        Theme theme = findById(id);
        checkNotNull(theme, "Theme not found");

        delete(theme);
    }

    @Override
    public void delete(Theme theme) {
        Collection<ExamRule> examRules = CollectionUtils.emptyIfNull(theme.getExamRules());
        for (ExamRule examRule : examRules) {
            examRuleService.delete(examRule);
        }
        for (Task task : CollectionUtils.emptyIfNull(theme.getTasks())) {
            taskService.delete(task);
        }
        super.delete(theme);
    }

    public List<TaskBean> findTasks(Long themeId) {
        Theme theme = findById(themeId);
        checkNotNull(theme, "Theme not found");

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

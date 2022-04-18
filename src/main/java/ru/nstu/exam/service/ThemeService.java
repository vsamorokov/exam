package ru.nstu.exam.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.nstu.exam.bean.ThemeBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Theme;
import ru.nstu.exam.repository.ThemeRepository;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class ThemeService extends BasePersistentService<Theme, ThemeBean, ThemeRepository> {

    private final DisciplineService disciplineService;

    public ThemeService(ThemeRepository repository, DisciplineService disciplineService) {
        super(repository);
        this.disciplineService = disciplineService;
    }

    public ThemeBean findOne(Long themeId) {
        Theme theme = findById(themeId);
        if (theme == null) {
            userError("Theme not found");
        }
        return map(theme);
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

    @Override
    protected ThemeBean map(Theme entity) {
        ThemeBean themeBean = new ThemeBean();
        themeBean.setId(entity.getId());
        themeBean.setName(entity.getName());
        themeBean.setDisciplineId(entity.getDiscipline().getId());
        return themeBean;
    }

    @Override
    protected Theme map(ThemeBean bean) {
        Theme theme = new Theme();
        theme.setName(bean.getName());
        return theme;
    }
}

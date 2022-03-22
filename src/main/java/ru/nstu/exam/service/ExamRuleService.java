package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.DisciplineBean;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.bean.ThemeBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.ExamRule;
import ru.nstu.exam.entity.Theme;
import ru.nstu.exam.repository.ExamRuleRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class ExamRuleService extends BasePersistentService<ExamRule, ExamRuleBean, ExamRuleRepository> {
    private final ThemeService themeService;
    private final DisciplineService disciplineService;

    public ExamRuleService(ExamRuleRepository repository, ThemeService themeService, DisciplineService disciplineService) {
        super(repository);
        this.themeService = themeService;
        this.disciplineService = disciplineService;
    }

    public ExamRuleBean createExamRule(ExamRuleBean examRuleBean) {
        DisciplineBean disciplineBean = examRuleBean.getDiscipline();
        if(disciplineBean == null) {
            userError("Exam rule must have discipline");
        }
        if(disciplineBean.getId() == null) {
            userError("Discipline must have an id");
        }
        Discipline discipline = disciplineService.findById(disciplineBean.getId());
        if(discipline == null) {
            userError("No discipline with specified id");
        }
        List<ThemeBean> themeBeans = examRuleBean.getThemes();
        if(CollectionUtils.isEmpty(themeBeans)){
            userError("Exam rule must have at least 1 theme");
        }
        List<Theme> themes = new ArrayList<>(themeBeans.size());
        for (ThemeBean themeBean : themeBeans) {
            if(themeBean.getId() == null) {
                userError("Theme must have an id");
            }
            Theme theme = themeService.findById(themeBean.getId());
            if(theme == null) {
                userError("No theme with id " + themeBean.getId());
            }
            themes.add(theme);
        }

        ExamRule examRule = map(examRuleBean);
        examRule.setDiscipline(discipline);
        examRule.setThemes(themes);

        return map(save(examRule));
    }

    public List<ExamRuleBean> findByDiscipline(Long disciplineId) {
        if (disciplineId == null) {
            return null;
        }
        Discipline discipline = disciplineService.getById(disciplineId);
        List<ExamRule> examRule = getRepository().findAllByDiscipline(discipline);
        return mapToBeans(examRule);
    }

    @Override
    protected ExamRuleBean map(ExamRule entity) {
        ExamRuleBean examRuleBean = new ExamRuleBean();
        examRuleBean.setId(entity.getId());
        examRuleBean.setName(entity.getName());
        examRuleBean.setDuration(entity.getDuration());
        examRuleBean.setExerciseCount(entity.getExerciseCount());
        examRuleBean.setQuestionCount(entity.getQuestionCount());
        examRuleBean.setMinimalRating(entity.getMinimalRating());
        examRuleBean.setDiscipline(disciplineService.map(entity.getDiscipline()));
        examRuleBean.setThemes(themeService.mapToBeans(entity.getThemes()));
        return examRuleBean;
    }

    @Override
    protected ExamRule map(ExamRuleBean bean) {
        ExamRule examRule = new ExamRule();
        examRule.setName(bean.getName());
        examRule.setExerciseCount(bean.getExerciseCount());
        examRule.setQuestionCount(bean.getQuestionCount());
        examRule.setDuration(bean.getDuration());
        examRule.setMinimalRating(bean.getMinimalRating());
        return examRule;
    }
}

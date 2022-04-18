package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.CreateExamRuleBean;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.ExamRule;
import ru.nstu.exam.entity.RatingSystem;
import ru.nstu.exam.entity.Theme;
import ru.nstu.exam.repository.ExamRuleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class ExamRuleService extends BasePersistentService<ExamRule, ExamRuleBean, ExamRuleRepository> {
    private final ThemeService themeService;
    private final DisciplineService disciplineService;
    private final RatingSystemService ratingSystemService;

    public ExamRuleService(ExamRuleRepository repository, ThemeService themeService, DisciplineService disciplineService, RatingSystemService ratingSystemService) {
        super(repository);
        this.themeService = themeService;
        this.disciplineService = disciplineService;
        this.ratingSystemService = ratingSystemService;
    }

    public ExamRuleBean findOne(Long id) {
        ExamRule examRule = findById(id);
        if (examRule == null) {
            userError("Exam rule not found");
        }
        return map(examRule);
    }

    public ExamRuleBean createExamRule(CreateExamRuleBean bean) {
        Discipline discipline = disciplineService.findById(bean.getDisciplineId());
        if (discipline == null) {
            userError("No discipline with specified id");
        }
        RatingSystem ratingSystem = ratingSystemService.findById(bean.getRatingSystemId());
        if (ratingSystem == null) {
            userError("No rating system with specified id");
        }
        List<Long> themeIds = bean.getThemeIds();
        if (CollectionUtils.isEmpty(themeIds)) {
            userError("Exam rule must have at least 1 theme");
        }
        List<Theme> themes = new ArrayList<>(themeIds.size());
        for (Long themeId : themeIds) {
            Theme theme = themeService.findById(themeId);
            if (theme == null) {
                userError("No theme with id " + themeId);
            }
            themes.add(theme);
        }

        ExamRule examRule = new ExamRule();
        examRule.setName(bean.getName());
        examRule.setExerciseCount(bean.getExerciseCount());
        examRule.setQuestionCount(bean.getQuestionCount());
        examRule.setDuration(bean.getDuration());
        examRule.setMinimalRating(bean.getMinimalRating());
        examRule.setDiscipline(discipline);
        examRule.setThemes(themes);
        examRule.setRatingSystem(ratingSystem);

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
        examRuleBean.setDisciplineId(entity.getDiscipline().getId());
        examRuleBean.setThemeIds(entity.getThemes().stream().map(AbstractPersistable::getId).collect(Collectors.toList()));
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

package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.bean.full.FullExamRuleBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.ExamRule;
import ru.nstu.exam.entity.GroupRating;
import ru.nstu.exam.entity.Theme;
import ru.nstu.exam.repository.ExamRuleRepository;
import ru.nstu.exam.service.mapper.FullExamRuleMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.nstu.exam.exception.ExamException.userError;
import static ru.nstu.exam.utils.Utils.checkNotNull;

@Service
public class ExamRuleService extends BasePersistentService<ExamRule, ExamRuleBean, ExamRuleRepository> {
    private final ThemeService themeService;
    private final DisciplineService disciplineService;
    private final FullExamRuleMapper fullExamRuleMapper;
    private final GroupRatingService groupRatingService;

    public ExamRuleService(ExamRuleRepository repository, ThemeService themeService, DisciplineService disciplineService, FullExamRuleMapper fullExamRuleMapper, @Lazy GroupRatingService groupRatingService) {
        super(repository);
        this.themeService = themeService;
        this.disciplineService = disciplineService;
        this.fullExamRuleMapper = fullExamRuleMapper;
        this.groupRatingService = groupRatingService;
    }

    public ExamRuleBean findOne(Long id) {
        ExamRule examRule = findById(id);
        if (examRule == null) {
            userError("Exam rule not found");
        }
        return map(examRule);
    }

    public FullExamRuleBean findFull(Long id, int level) {
        ExamRule examRule = findById(id);
        checkNotNull(examRule, "Exam rule not found");
        return fullExamRuleMapper.map(examRule, level);
    }

    public ExamRuleBean createExamRule(ExamRuleBean bean) {
        ExamRule examRule = new ExamRule();
        fillExamRule(examRule, bean);
        return map(save(examRule));
    }

    public ExamRuleBean updateExamRule(ExamRuleBean bean) {
        ExamRule examRule = findById(bean.getId());
        checkNotNull(examRule, "Exam rule not found");
        fillExamRule(examRule, bean);
        return map(save(examRule));
    }

    private void fillExamRule(ExamRule examRule, ExamRuleBean bean) {

        Discipline discipline = disciplineService.findById(bean.getDisciplineId());
        checkNotNull(discipline, String.format("Discipline with id %s not found", bean.getDisciplineId()));

        List<Long> themeIds = bean.getThemeIds();
        List<Theme> themes = new ArrayList<>(themeIds.size());
        for (Long themeId : themeIds) {
            Theme theme = themeService.findById(themeId);
            checkNotNull(theme, String.format("Theme with id %s not found", themeId));
            themes.add(theme);
        }

        examRule.setName(bean.getName());
        examRule.setMaximumExamRating(bean.getMaximumExamRating());
        examRule.setMinimalExamRating(bean.getMinimalExamRating());
        examRule.setDuration(bean.getDuration());
        examRule.setMinimalSemesterRating(bean.getMinimalSemesterRating());
        examRule.setExercisesRatingSum(bean.getExercisesRatingSum());
        examRule.setQuestionsRatingSum(bean.getQuestionsRatingSum());
        examRule.setSingleExerciseDefaultRating(bean.getSingleExerciseDefaultRating());
        examRule.setSingleQuestionDefaultRating(bean.getSingleQuestionDefaultRating());
        examRule.setDiscipline(discipline);
        examRule.setThemes(themes);
    }

    public void delete(Long id) {
        ExamRule examRule = findById(id);
        checkNotNull(examRule, "Exam rule not found");
        delete(examRule);
    }

    @Override
    public void delete(ExamRule examRule) {
        for (GroupRating groupRating : CollectionUtils.emptyIfNull(examRule.getGroupRatings())) {
            groupRatingService.delete(groupRating);
        }
        super.delete(examRule);
    }

    public List<ExamRuleBean> findByDiscipline(Long disciplineId) {
        Discipline discipline = disciplineService.findById(disciplineId);
        checkNotNull(discipline, "Discipline not found");
        List<ExamRule> examRule = getRepository().findAllByDiscipline(discipline);
        return mapToBeans(examRule);
    }

    @Override
    protected ExamRuleBean map(ExamRule entity) {
        ExamRuleBean examRuleBean = new ExamRuleBean();
        examRuleBean.setId(entity.getId());
        examRuleBean.setName(entity.getName());
        examRuleBean.setMaximumExamRating(entity.getMaximumExamRating());
        examRuleBean.setMinimalExamRating(entity.getMinimalExamRating());
        examRuleBean.setDuration(entity.getDuration());
        examRuleBean.setMinimalSemesterRating(entity.getMinimalSemesterRating());
        examRuleBean.setExercisesRatingSum(entity.getExercisesRatingSum());
        examRuleBean.setQuestionsRatingSum(entity.getQuestionsRatingSum());
        examRuleBean.setSingleExerciseDefaultRating(entity.getSingleExerciseDefaultRating());
        examRuleBean.setSingleQuestionDefaultRating(entity.getSingleQuestionDefaultRating());
        examRuleBean.setDisciplineId(entity.getDiscipline().getId());
        examRuleBean.setThemeIds(entity.getThemes().stream().map(AbstractPersistable::getId).collect(Collectors.toList()));
        return examRuleBean;
    }

    @Override
    protected ExamRule map(ExamRuleBean bean) {
        return null;
    }

}

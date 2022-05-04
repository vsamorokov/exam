package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.bean.FullExamRuleBean;
import ru.nstu.exam.entity.*;
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
    private final RatingSystemService ratingSystemService;
    private final ExamService examService;
    private final FullExamRuleMapper fullExamRuleMapper;

    public ExamRuleService(ExamRuleRepository repository, ThemeService themeService, DisciplineService disciplineService, RatingSystemService ratingSystemService, @Lazy ExamService examService, FullExamRuleMapper fullExamRuleMapper) {
        super(repository);
        this.themeService = themeService;
        this.disciplineService = disciplineService;
        this.ratingSystemService = ratingSystemService;
        this.examService = examService;
        this.fullExamRuleMapper = fullExamRuleMapper;
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

    public ExamRuleBean updateExamRule(Long id, ExamRuleBean bean) {
        ExamRule examRule = findById(id);
        if (examRule == null) {
            userError("Exam rule not found");
        }

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

    public void delete(Long id) {
        ExamRule examRule = findById(id);
        if (examRule == null) {
            userError("Exam rule not found");
        }
        delete(examRule);
    }

    @Override
    public void delete(ExamRule examRule) {
        for (Exam exam : CollectionUtils.emptyIfNull(examRule.getExams())) {
            examService.delete(exam);
        }
        super.delete(examRule);
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

package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.bean.full.FullExamRuleBean;
import ru.nstu.exam.bean.full.FullThemeBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.ExamRule;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullExamRuleMapper implements Mapper<FullExamRuleBean, ExamRule> {

    private final FullDisciplineMapper disciplineMapper;
    private final FullThemeMapper themeMapper;

    @Override
    public FullExamRuleBean map(ExamRule entity, int level) {
        FullExamRuleBean fullExamRuleBean = new FullExamRuleBean();
        if (level >= 0) {
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
            fullExamRuleBean.setExamRule(examRuleBean);
        }
        if (level >= 1) {
            Discipline discipline = entity.getDiscipline();
            if (discipline != null) {
                fullExamRuleBean.setDiscipline(disciplineMapper.map(discipline, level - 1));
            }
            List<FullThemeBean> themes = CollectionUtils.emptyIfNull(entity.getThemes()).stream()
                    .map(t -> themeMapper.map(t, level - 1))
                    .collect(Collectors.toList());
            fullExamRuleBean.setThemes(themes);
        }
        return fullExamRuleBean;
    }
}

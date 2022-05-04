package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.bean.FullExamRuleBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.ExamRule;
import ru.nstu.exam.entity.Theme;

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
            examRuleBean.setDuration(entity.getDuration());
            examRuleBean.setExerciseCount(entity.getExerciseCount());
            examRuleBean.setQuestionCount(entity.getQuestionCount());
            examRuleBean.setMinimalRating(entity.getMinimalRating());
            examRuleBean.setDisciplineId(entity.getDiscipline() == null ? null : entity.getDiscipline().getId());
            examRuleBean.setThemeIds(entity.getThemes() == null ? null :
                    entity.getThemes().stream().map(AbstractPersistable::getId).collect(Collectors.toList()));
            fullExamRuleBean.setExamRule(examRuleBean);
        }
        if (level >= 1) {
            Discipline discipline = entity.getDiscipline();
            if (discipline != null) {
                fullExamRuleBean.setDiscipline(disciplineMapper.map(discipline, level - 1));
            }
            List<Theme> themes = entity.getThemes();
            if (CollectionUtils.isNotEmpty(themes)) {
                fullExamRuleBean.setThemes(
                        themes.stream()
                                .map(t -> themeMapper.map(t, level - 1))
                                .collect(Collectors.toList())
                );
            }
        }
        return fullExamRuleBean;
    }
}

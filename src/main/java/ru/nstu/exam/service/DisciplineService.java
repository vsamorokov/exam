package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.DisciplineBean;
import ru.nstu.exam.bean.ThemeBean;
import ru.nstu.exam.bean.full.FullDisciplineBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Exam;
import ru.nstu.exam.entity.ExamRule;
import ru.nstu.exam.entity.Theme;
import ru.nstu.exam.repository.DisciplineRepository;
import ru.nstu.exam.service.mapper.FullDisciplineMapper;

import java.util.List;

import static ru.nstu.exam.exception.ExamException.userError;
import static ru.nstu.exam.utils.Utils.checkNotNull;

@Service
public class DisciplineService extends BasePersistentService<Discipline, DisciplineBean, DisciplineRepository> {

    private final FullDisciplineMapper disciplineMapper;
    private final ThemeService themeService;
    private final ExamService examService;
    private final ExamRuleService examRuleService;

    public DisciplineService(DisciplineRepository repository, FullDisciplineMapper disciplineMapper, @Lazy ThemeService themeService, @Lazy ExamService examService, @Lazy ExamRuleService examRuleService) {
        super(repository);
        this.disciplineMapper = disciplineMapper;
        this.themeService = themeService;
        this.examService = examService;
        this.examRuleService = examRuleService;
    }

    public FullDisciplineBean findFull(Long disciplineId, int level) {
        Discipline discipline = findById(disciplineId);
        if (discipline == null) {
            userError("Discipline not found");
        }
        return disciplineMapper.map(discipline, level);
    }

    public DisciplineBean findOne(Long disciplineId) {
        Discipline discipline = findById(disciplineId);
        checkNotNull(discipline, "Discipline not found");
        if (discipline == null) {
            userError("Discipline not found");
        }
        return map(discipline);
    }

    public DisciplineBean createDiscipline(DisciplineBean disciplineBean) {
        return map(save(map(disciplineBean)));
    }

    public DisciplineBean update(DisciplineBean disciplineBean) {
        Discipline discipline = findById(disciplineBean.getId());
        checkNotNull(discipline, "Discipline not found");

        discipline.setName(disciplineBean.getName());
        return map(save(discipline));
    }

    public void delete(Long id) {
        Discipline discipline = findById(id);
        checkNotNull(discipline, "Discipline not found");
        delete(discipline);
    }


    public List<ThemeBean> getThemes(Long disciplineId) {
        Discipline discipline = findById(disciplineId);
        if (discipline == null) {
            userError("Discipline not found");
        }
        return themeService.mapToBeans(discipline.getThemes());
    }

    @Override
    public void delete(Discipline discipline) {
        for (Theme theme : CollectionUtils.emptyIfNull(discipline.getThemes())) {
            themeService.delete(theme);
        }
        for (Exam exam : CollectionUtils.emptyIfNull(discipline.getExams())) {
            examService.delete(exam);
        }
        for (ExamRule examRule : CollectionUtils.emptyIfNull(discipline.getExamRules())) {
            examRuleService.delete(examRule);
        }
        super.delete(discipline);
    }

    @Override
    protected DisciplineBean map(Discipline entity) {
        DisciplineBean disciplineBean = new DisciplineBean();
        disciplineBean.setId(entity.getId());
        disciplineBean.setName(entity.getName());
        return disciplineBean;
    }

    @Override
    protected Discipline map(DisciplineBean bean) {
        Discipline discipline = new Discipline();
        discipline.setName(bean.getName());
        return discipline;
    }

}

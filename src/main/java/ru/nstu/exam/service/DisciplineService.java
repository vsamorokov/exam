package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.DisciplineBean;
import ru.nstu.exam.bean.FullDisciplineBean;
import ru.nstu.exam.bean.ThemeBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.repository.DisciplineRepository;
import ru.nstu.exam.service.mapper.FullDisciplineMapper;

import java.util.List;

import static ru.nstu.exam.exception.ExamException.userError;

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

    public FullDisciplineBean findOne(Long disciplineId, int level) {
        Discipline discipline = findById(disciplineId);
        if (discipline == null) {
            userError("Discipline not found");
        }
        return disciplineMapper.map(discipline, level);
    }

    public DisciplineBean createDiscipline(DisciplineBean disciplineBean) {
        return map(save(map(disciplineBean)));
    }

    public DisciplineBean update(Long id, DisciplineBean disciplineBean) {
        Discipline discipline = findById(id);
        if (discipline == null) {
            userError("Discipline not found");
        }
        discipline.setName(disciplineBean.getName());
        return map(save(discipline));
    }

    public void delete(Long id) {
        Discipline discipline = findById(id);
        if (discipline == null) {
            userError("Discipline not found");
        }
        delete(discipline);
    }

    public List<DisciplineBean> findByTeacher(Teacher teacher) {
        return mapToBeans(getRepository().findByTeachersContaining(teacher));
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

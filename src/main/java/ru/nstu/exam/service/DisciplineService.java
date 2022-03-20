package ru.nstu.exam.service;

import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.DisciplineBean;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.ExamRule;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.Teacher;
import ru.nstu.exam.repository.DisciplineRepository;

import java.util.List;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class DisciplineService extends BasePersistentService<Discipline, DisciplineBean, DisciplineRepository> {

    public DisciplineService(DisciplineRepository repository) {
        super(repository);
    }

    public DisciplineBean createDiscipline(DisciplineBean disciplineBean){
        return map(save(map(disciplineBean)));
    }

    public List<DisciplineBean> findByTeacher(Teacher teacher){
        return mapToBeans(getRepository().findByTeachersContaining(teacher));
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

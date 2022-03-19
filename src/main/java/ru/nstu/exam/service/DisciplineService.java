package ru.nstu.exam.service;

import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.DisciplineBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.Teacher;
import ru.nstu.exam.repository.DisciplineRepository;

import java.util.List;

@Service
public class DisciplineService extends BasePersistentService<Discipline, DisciplineBean, DisciplineRepository> {
    private final GroupService groupService;
    private final TeacherService teacherService;

    public DisciplineService(DisciplineRepository repository, GroupService groupService, TeacherService teacherService) {
        super(repository);
        this.groupService = groupService;
        this.teacherService = teacherService;
    }

    public DisciplineBean createDiscipline(DisciplineBean disciplineBean){
        return null;
    }

    public List<DisciplineBean> findByGroup(Long groupId) {
        if (groupId == null) {
            return null;
        }
        Group group = groupService.getById(groupId);
        List<Discipline> disciplines = getRepository().findByGroupsContaining(group);
        return mapToBeans(disciplines);
    }

    public List<DisciplineBean> findByTeacher(Long teacherId) {
        if (teacherId == null) {
            return null;
        }
        Teacher teacher = teacherService.getById(teacherId);
        List<Discipline> disciplines = getRepository().findByTeachersContaining(teacher);
        return mapToBeans(disciplines);
    }

    @Override
    protected DisciplineBean map(Discipline entity) {
        DisciplineBean disciplineBean = new DisciplineBean();
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

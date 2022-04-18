package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AccountBean;
import ru.nstu.exam.bean.CreateTeacherBean;
import ru.nstu.exam.bean.DisciplineBean;
import ru.nstu.exam.bean.TeacherBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Teacher;
import ru.nstu.exam.repository.TeacherRepository;
import ru.nstu.exam.security.UserRole;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class TeacherService extends BasePersistentService<Teacher, TeacherBean, TeacherRepository> {
    private final AccountService accountService;
    private final DisciplineService disciplineService;

    public TeacherService(TeacherRepository repository, AccountService accountService, DisciplineService disciplineService) {
        super(repository);
        this.accountService = accountService;
        this.disciplineService = disciplineService;
    }

    public TeacherBean getSelf(Account account) {
        return map(getRepository().findByAccount(account));
    }

    public Teacher findByAccount(Account account) {
        return getRepository().findByAccount(account);
    }

    public List<TeacherBean> addTeachers(List<CreateTeacherBean> teachers) {
        return teachers.stream().map(this::createTeacher).collect(Collectors.toList());
    }

    public TeacherBean createTeacher(CreateTeacherBean teacherBean) {
        List<Long> disciplineIds = teacherBean.getDisciplineIds();
        if (CollectionUtils.isEmpty(disciplineIds)) {
            userError("Teacher must have disciplines");
        }
        List<Discipline> disciplines = new ArrayList<>(disciplineIds.size());
        for (Long id : disciplineIds) {
            Discipline discipline = disciplineService.findById(id);
            if (discipline == null) {
                userError("No discipline found");
            }
            disciplines.add(discipline);
        }

        if (teacherBean.getAccount() == null) {
            userError("Teacher must have account");
        }
        if (!teacherBean.getAccount().getRoles().contains(UserRole.ROLE_TEACHER)) {
            userError("Teacher must contain 'ROLE_TEACHER' role");
        }
        if (teacherBean.getAccount().getRoles().contains(UserRole.ROLE_STUDENT)) {
            userError("Teacher must not contain 'ROLE_STUDENT' role");
        }
        AccountBean accountBean = accountService.createAccount(teacherBean.getAccount());
        Account account = accountService.getById(accountBean.getId());

        Teacher teacher = new Teacher();
        teacher.setAccount(account);
        teacher.setDisciplines(disciplines);
        return map(save(teacher));
    }

    public List<DisciplineBean> findDisciplines(Account account) {
        Teacher teacher = findByAccount(account);
        if(teacher == null) {
            userError("No teacher found");
        }
        return disciplineService.findByTeacher(teacher);
    }

    @Override
    protected TeacherBean map(Teacher entity) {
        TeacherBean teacherBean = new TeacherBean();
        teacherBean.setId(entity.getId());
        teacherBean.setAccount(accountService.map(entity.getAccount()));
        return teacherBean;
    }

    @Override
    protected Teacher map(TeacherBean bean) {
        return new Teacher();
    }

}

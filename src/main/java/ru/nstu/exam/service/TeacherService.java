package ru.nstu.exam.service;

import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AccountBean;
import ru.nstu.exam.bean.CreateTeacherBean;
import ru.nstu.exam.bean.TeacherBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.entity.Teacher;
import ru.nstu.exam.repository.TeacherRepository;
import ru.nstu.exam.security.UserRole;

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

    public Teacher findByAccount(Account account) {
        return getRepository().findByAccount(account);
    }

    public List<TeacherBean> addTeachers(List<CreateTeacherBean> teachers) {
        return teachers.stream().map(this::createTeacher).collect(Collectors.toList());
    }

    public TeacherBean createTeacher(CreateTeacherBean teacherBean) {
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
        return map(save(teacher));
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

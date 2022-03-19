package ru.nstu.exam.service;

import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AccountBean;
import ru.nstu.exam.bean.StudentBean;
import ru.nstu.exam.bean.ChangePasswordBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.Student;
import ru.nstu.exam.repository.StudentRepository;
import ru.nstu.exam.security.UserRole;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class StudentService extends BasePersistentService<Student, StudentBean, StudentRepository> {

    private final GroupService groupService;
    private final AccountService accountService;


    public StudentService(StudentRepository repository, GroupService groupService, AccountService accountService) {
        super(repository);
        this.groupService = groupService;
        this.accountService = accountService;
    }

    public List<StudentBean> findByGroup(Long groupId) {
        Group group = groupService.getById(groupId);
        return getRepository().findAllByGroup(group).stream().map(this::map).collect(Collectors.toList());
    }

    public List<StudentBean> addStudents(List<StudentBean> students) {
        return students.stream().map(this::createStudent).collect(Collectors.toList());
    }

    public StudentBean createStudent(StudentBean studentBean) {
        if (studentBean.getGroup().getId() == null) {
            userError("Group id must be specified");
        }
        Group group = groupService.getById(studentBean.getGroup().getId());
        if (group == null) {
            userError("No group with specified id");
        }
        if(studentBean.getAccount() == null) {
            userError("Student must have account");
        }
        if (!Collections.singleton(UserRole.ROLE_STUDENT).containsAll(studentBean.getAccount().getRoles())) {
            userError("Student must have single 'ROLE_STUDENT' role");
        }
        AccountBean accountBean = accountService.createAccount(studentBean.getAccount());
        Account account = accountService.getById(accountBean.getId());

        Student student = map(studentBean);
        student.setAccount(account);
        student.setGroup(group);
        return map(save(student));
    }

    @Override
    protected StudentBean map(Student entity) {
        StudentBean studentBean = new StudentBean();
        studentBean.setId(entity.getId());
        studentBean.setAccount(accountService.map(entity.getAccount()));
        studentBean.setGroup(groupService.map(entity.getGroup()));
        return studentBean;
    }

    @Override
    protected Student map(StudentBean bean) {
        return new Student();
    }

}

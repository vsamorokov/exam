package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AccountBean;
import ru.nstu.exam.bean.StudentBean;
import ru.nstu.exam.bean.StudentTicketBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.Student;
import ru.nstu.exam.entity.Ticket;
import ru.nstu.exam.repository.StudentRepository;
import ru.nstu.exam.security.UserRole;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.nstu.exam.exception.ExamException.userError;
import static ru.nstu.exam.utils.Utils.checkNotNull;

@Service
public class StudentService extends BasePersistentService<Student, StudentBean, StudentRepository> {

    private final GroupService groupService;
    private final AccountService accountService;
    private final TicketService ticketService;

    public StudentService(StudentRepository repository, GroupService groupService, AccountService accountService, TicketService ticketService) {
        super(repository);
        this.groupService = groupService;
        this.accountService = accountService;
        this.ticketService = ticketService;
    }

    public List<StudentBean> findByGroup(Long groupId) {
        Group group = groupService.getById(groupId);
        return getRepository().findAllByGroup(group).stream().map(this::map).collect(Collectors.toList());
    }

    public List<StudentBean> addStudents(List<StudentBean> students) {
        return students.stream().map(this::createStudent).collect(Collectors.toList());
    }

    public StudentBean createStudent(StudentBean studentBean) {
        Group group = groupService.getById(studentBean.getGroupId());
        if (group == null) {
            userError("No group with specified id");
        }
        if (studentBean.getAccount() == null) {
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

    public void delete(Long id) {
        Student student = findById(id);
        if (student == null) {
            userError("Student not found");
        }
        delete(student);
    }

    @Override
    public void delete(Student student) {
        for (Ticket ticket : CollectionUtils.emptyIfNull(student.getTickets())) {
            ticketService.delete(ticket);
        }
        accountService.delete(student.getAccount());
        super.delete(student);
    }

    public StudentBean findOne(Long id) {
        Student student = findById(id);
        checkNotNull(student, "Student not found");
        return map(student);
    }

    public StudentBean findByAccount(Account account) {
        return map(getRepository().findByAccount(account));
    }

    public List<StudentTicketBean> getTickets(Account account) {
        Student student = getRepository().findByAccount(account);
        if (student == null) {
            userError("No student found");
        }
        return ticketService.getStudentTickets(student);
    }

    @Override
    protected StudentBean map(Student entity) {
        StudentBean studentBean = new StudentBean();
        studentBean.setId(entity.getId());
        studentBean.setAccount(accountService.map(entity.getAccount()));
        studentBean.setGroupId(entity.getGroup().getId());
        return studentBean;
    }

    @Override
    protected Student map(StudentBean bean) {
        return new Student();
    }

}

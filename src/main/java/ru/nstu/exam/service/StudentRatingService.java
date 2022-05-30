package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.*;
import ru.nstu.exam.bean.full.FullStudentRatingBean;
import ru.nstu.exam.bean.student.StudentAnswerBean;
import ru.nstu.exam.bean.student.StudentExamInfoBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.enums.ExamState;
import ru.nstu.exam.enums.StudentRatingState;
import ru.nstu.exam.enums.TaskType;
import ru.nstu.exam.repository.StudentRatingRepository;
import ru.nstu.exam.service.listener.AnswerStateChangeListener;
import ru.nstu.exam.service.listener.ExamStateChangeListener;
import ru.nstu.exam.service.mapper.FullStudentRatingMapper;

import java.util.List;
import java.util.stream.Collectors;

import static ru.nstu.exam.enums.StudentRatingState.*;
import static ru.nstu.exam.exception.ExamException.userError;
import static ru.nstu.exam.utils.Utils.*;

@Service
public class StudentRatingService
        extends BasePersistentService<StudentRating, StudentRatingBean, StudentRatingRepository>
        implements ExamStateChangeListener, AnswerStateChangeListener {
    private final AnswerService answerService;
    private final FullStudentRatingMapper ticketMapper;

    public StudentRatingService(StudentRatingRepository repository, AnswerService answerService, FullStudentRatingMapper ticketMapper) {
        super(repository);
        this.answerService = answerService;
        this.ticketMapper = ticketMapper;
    }

    public StudentRatingBean findOne(Long id) {
        StudentRating studentRating = findById(id);
        checkNotNull(studentRating, "Ticket not found");
        return map(studentRating);
    }

    public FullStudentRatingBean findFull(Long id, int level) {
        StudentRating studentRating = findById(id);
        checkNotNull(studentRating, "Ticket not found");
        return ticketMapper.map(studentRating, level);
    }

    public void create(Student student) {
        Group group = student.getGroup();
        for (GroupRating groupRating : CollectionUtils.emptyIfNull(group.getGroupRatings())) {
            if (getRepository().existsByStudentAndGroupRating(student, groupRating)) {
                continue;
            }
            StudentRating studentRating = new StudentRating();
            studentRating.setStudent(student);
            studentRating.setStudentRatingState(NOT_ALLOWED);
            studentRating.setGroupRating(groupRating);
            studentRating.setQuestionRating(0);
            studentRating.setExerciseRating(0);
            studentRating.setSemesterRating(0);
            save(studentRating);
        }
    }

    public void create(GroupRating groupRating) {
        List<Student> students = groupRating.getGroup().getStudents();
        for (Student student : students) {
            StudentRating studentRating = new StudentRating();
            studentRating.setStudent(student);
            studentRating.setStudentRatingState(NOT_ALLOWED);
            studentRating.setGroupRating(groupRating);
            studentRating.setQuestionRating(0);
            studentRating.setExerciseRating(0);
            studentRating.setSemesterRating(0);
            save(studentRating);
        }
    }

    public StudentRatingBean update(StudentRatingBean bean) {
        StudentRating rating = findById(bean.getId());
        checkNotNull(rating, String.format("Rating with id %d not found", bean.getId()));
        Integer minRating = rating.getGroupRating().getExamRule().getMinimalSemesterRating();

        checkTrue(rating.getStudentRatingState().in(NOT_ALLOWED, ALLOWED), "Wrong state");

        rating.setSemesterRating(bean.getSemesterRating());
        if (bean.getSemesterRating() >= minRating) {
            rating.setStudentRatingState(ALLOWED);
        } else {
            rating.setStudentRatingState(NOT_ALLOWED);
        }
        return map(save(rating));
    }

    public List<StudentRatingBean> findByExam(Exam exam) {
        return mapToBeans(exam.getStudentRatings());
    }

    public List<StudentExamInfoBean> getStudentExamInfo(Student student) {
        return getRepository().findAllByStudent(student).stream()
                .filter(sr -> sr.getStudentRatingState().in(ASSIGNED_TO_EXAM, WAITING_TO_APPEAR, PASSING, FINISHED, RATED))
                .map(this::mapToStudentBean)
                .collect(Collectors.toList());
    }

    public List<StudentAnswerBean> getStudentAnswers(Long studentRatingId, Pageable pageable) {
        StudentRating studentRating = findById(studentRatingId);
        checkNotNull(studentRating, "Student rating not found");

        ExamState state = studentRating.getExam().getState();
        if (state.isBefore(ExamState.PROGRESS)) {
            userError("Exam did not start yet");
        }
        return answerService.findByStudentRating(studentRating, pageable);
    }

    public void delete(Long studentRatingId) {
        StudentRating studentRating = findById(studentRatingId);
        checkNotNull(studentRating, "Student rating not found");
        delete(studentRating);
    }

    @Override
    public void delete(StudentRating studentRating) {
        checkTrue(studentRating.getStudentRatingState().in(EMPTY, NOT_ALLOWED), "Wrong state");

        for (Answer answer : CollectionUtils.emptyIfNull(studentRating.getAnswers())) {
            answerService.delete(answer);
        }
        super.delete(studentRating);
    }

    @Override
    public void examCreated(Exam exam) {
        List<Long> ids = exam.getStudentRatings().stream().map(AbstractPersistable::getId).collect(Collectors.toList());
        for (Long id : ids) {
            StudentRating studentRating = findById(id);
            if (studentRating == null) {
                continue;
            }
            studentRating.setExam(exam);
            studentRating.setStudentRatingState(StudentRatingState.ASSIGNED_TO_EXAM);
            save(studentRating);
        }
    }

    @Override
    public void examStarted(Exam exam) {
        for (StudentRating studentRating : exam.getStudentRatings()) {
            studentRating.setStudentRatingState(StudentRatingState.WAITING_TO_APPEAR);
            save(studentRating);
        }
    }

    @Override
    public void examFinished(Exam exam) {
        for (StudentRating studentRating : exam.getStudentRatings()) {
            if (StudentRatingState.PASSING.equals(studentRating.getStudentRatingState())) {
                studentRating.setStudentRatingState(FINISHED);
                save(studentRating);
            } else if (StudentRatingState.WAITING_TO_APPEAR.equals(studentRating.getStudentRatingState())) {
                studentRating.setStudentRatingState(StudentRatingState.ABSENT);
                save(studentRating);
            }
        }
    }

    public StudentRatingBean updateState(StudentRatingBean bean) {
        StudentRating studentRating = findById(bean.getId());
        checkNotNull(studentRating, String.format("Student rating with id %s not found", bean.getId()));

        StudentRatingState newState = bean.getStudentRatingState();
        checkTrue(newState.allowedFor(studentRating), "Wrong state");

        if (PASSING.equals(newState)) {
            return map(startExamForStudent(studentRating));
        }

        studentRating.setStudentRatingState(newState);
        return map(save(studentRating));
    }

    private StudentRating startExamForStudent(StudentRating studentRating) {
        checkTrue(PASSING.allowedFor(studentRating), "Wrong state");
        StudentRatingState oldState = studentRating.getStudentRatingState();

        studentRating.setStudentRatingState(PASSING);
        StudentRating saved = save(studentRating);
        try {
            answerService.generateAnswers(saved);
        } catch (Exception e) {
            saved.setStudentRatingState(oldState);
            save(saved);
            throw e;
        }
        return saved;
    }

    /**
     * Recalculate all ratings
     */
    @Override
    public void answerStateChanged(Answer answer) {
        StudentRating studentRating = answer.getStudentRating();
        int questionRating = 0;
        int exerciseRating = 0;
        for (Answer studentAnswer : studentRating.getAnswers()) {
            if (studentAnswer.getTask().getTaskType().equals(TaskType.QUESTION)) {
                questionRating += studentAnswer.getRating();
            }
            if (studentAnswer.getTask().getTaskType().equals(TaskType.EXERCISE)) {
                exerciseRating += studentAnswer.getRating();
            }
        }
        studentRating.setQuestionRating(questionRating);
        studentRating.setExerciseRating(exerciseRating);
        save(studentRating);
    }

    @Override
    protected StudentRatingBean map(StudentRating entity) {
        StudentRatingBean bean = new StudentRatingBean();
        bean.setId(entity.getId());
        bean.setQuestionRating(entity.getQuestionRating());
        bean.setExerciseRating(entity.getExerciseRating());
        bean.setSemesterRating(entity.getSemesterRating());
        bean.setStudentId(entity.getStudent().getId());
        bean.setExamId(entity.getExam() == null ? null : entity.getExam().getId());
        bean.setStudentRatingState(entity.getStudentRatingState());
        bean.setGroupRatingId(entity.getGroupRating().getId());
        return bean;
    }

    public List<AnswerBean> getAnswers(StudentRating sr) {
        return answerService.getAnswersByStudentRating(sr);
    }

    private StudentExamInfoBean mapToStudentBean(StudentRating studentRating) {
        StudentExamInfoBean bean = new StudentExamInfoBean();

        bean.setId(studentRating.getId());
        bean.setSemesterRating(studentRating.getSemesterRating());
        bean.setQuestionRating(studentRating.getQuestionRating());
        bean.setExerciseRating(studentRating.getExerciseRating());
        bean.setStudentRatingState(studentRating.getStudentRatingState());
        bean.setGroupRatingId(studentRating.getGroupRating().getId());

        Exam exam = studentRating.getExam();

        ExamBean examBean = new ExamBean();
        examBean.setId(exam.getId());
        examBean.setName(exam.getName());
        examBean.setStart(toMillis(exam.getStart()));
        examBean.setEnd(toMillis(exam.getEnd()));
        examBean.setState(exam.getState());
        examBean.setDisciplineId(exam.getDiscipline().getId());
        examBean.setGroupId(exam.getGroup() == null ? null : exam.getGroup().getId());
        examBean.setOneGroup(exam.isOneGroup());
        bean.setExam(examBean);

        ExamRule examRule = studentRating.getGroupRating().getExamRule();

        ExamRuleBean examRuleBean = new ExamRuleBean();
        examRuleBean.setId(examRule.getId());
        examRuleBean.setName(examRule.getName());
        examRuleBean.setMaximumExamRating(examRule.getMaximumExamRating());
        examRuleBean.setMinimalExamRating(examRule.getMinimalExamRating());
        examRuleBean.setDuration(examRule.getDuration());
        examRuleBean.setMinimalSemesterRating(examRule.getMinimalSemesterRating());
        examRuleBean.setExercisesRatingSum(examRule.getExercisesRatingSum());
        examRuleBean.setQuestionsRatingSum(examRule.getQuestionsRatingSum());
        examRuleBean.setSingleExerciseDefaultRating(examRule.getSingleExerciseDefaultRating());
        examRuleBean.setSingleQuestionDefaultRating(examRule.getSingleQuestionDefaultRating());
        examRuleBean.setDisciplineId(examRule.getDiscipline().getId());
        examRuleBean.setThemeIds(examRule.getThemes().stream().map(AbstractPersistable::getId).collect(Collectors.toList()));
        bean.setExamRule(examRuleBean);

        Teacher teacher = exam.getTeacher();
        Account account = teacher.getAccount();
        TeacherBean teacherBean = new TeacherBean();
        teacherBean.setId(teacher.getId());
        AccountBean accountBean = new AccountBean();
        accountBean.setId(account.getId());
        accountBean.setUsername(account.getUsername());
        accountBean.setName(account.getName());
        accountBean.setSurname(account.getSurname());
        accountBean.setRoles(account.getRoles());
        teacherBean.setAccount(accountBean);

        bean.setTeacher(teacherBean);
        return bean;
    }

}

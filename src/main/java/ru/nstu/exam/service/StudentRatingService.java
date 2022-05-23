package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.StudentRatingBean;
import ru.nstu.exam.bean.full.FullStudentRatingBean;
import ru.nstu.exam.bean.student.StudentAnswerBean;
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
import static ru.nstu.exam.utils.Utils.checkNotNull;
import static ru.nstu.exam.utils.Utils.checkTrue;

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

    public List<StudentRatingBean> getStudentTickets(Student student) {
        return getRepository().findAllByStudent(student).stream()
                .filter(sr -> sr.getStudentRatingState().in(ASSIGNED_TO_EXAM, WAITING_TO_APPEAR, PASSING, FINISHED, RATED))
                .map(this::map)
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
        for (StudentRating studentRating : exam.getStudentRatings()) {
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
        studentRating.setStudentRatingState(PASSING);
        StudentRating saved = save(studentRating);
        answerService.generateAnswers(saved);
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

        return bean;
    }

    public List<AnswerBean> getAnswers(StudentRating sr) {
        return answerService.getAnswersByStudentRating(sr);
    }
//
//    private StudentTicketBean mapToStudentBean(StudentRating studentRating) {
//        Exam exam = studentRating.getExam();
//        Teacher teacher = exam.getTeacher();
//        Discipline discipline = exam.getDiscipline();
//        ExamRule examRule = exam.getExamRule();
//
//        StudentTicketBean bean = new StudentTicketBean();
//
//        bean.setId(studentRating.getId());
//        bean.setAllowed(studentRating.getAllowed());
//        bean.setExamRating(studentRating.getExamRating());
//        bean.setSemesterRating(studentRating.getSemesterRating());
//
//        ExamBean examBean = new ExamBean();
//        examBean.setId(exam.getId());
//        examBean.setName(exam.getName());
//        examBean.setStart(toMillis(exam.getStart()));
//        examBean.setEnd(toMillis(exam.getEnd()));
//        examBean.setState(exam.getState());
//        examBean.setExamRuleId(examRule.getId());
//        examBean.setDisciplineId(exam.getDiscipline().getId());
//        examBean.setGroupIds(exam.getGroups().stream().map(Group::getId).collect(Collectors.toList()));
//
//        bean.setExam(examBean);
//
//        bean.setDisciplineName(discipline.getName());
//
//        bean.setTeacher(teacherService.map(teacher));
//
//        bean.setQuestionRatingRange(examRule.getQuestionRatingRange());
//        bean.setExerciseRatingRange(examRule.getExerciseRatingRange());
//
//        return bean;
//    }

}

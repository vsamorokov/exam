package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.MessageBean;
import ru.nstu.exam.bean.full.FullAnswerBean;
import ru.nstu.exam.bean.student.StudentAnswerBean;
import ru.nstu.exam.bean.student.StudentTaskBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.enums.AnswerState;
import ru.nstu.exam.enums.ExamState;
import ru.nstu.exam.repository.AnswerRepository;
import ru.nstu.exam.security.UserRole;
import ru.nstu.exam.service.listener.ExamStateChangeListener;
import ru.nstu.exam.service.mapper.FullAnswerMapper;
import ru.nstu.exam.websocket.service.NotificationService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.nstu.exam.enums.AnswerState.*;
import static ru.nstu.exam.enums.TaskType.EXERCISE;
import static ru.nstu.exam.enums.TaskType.QUESTION;
import static ru.nstu.exam.utils.Utils.checkNotNull;
import static ru.nstu.exam.utils.Utils.checkTrue;

@Service
public class AnswerService extends BasePersistentService<Answer, AnswerBean, AnswerRepository> implements ExamStateChangeListener {
    private final Random random = new Random();

    private final MessageService messageService;
    private final FullAnswerMapper answerMapper;
    private final StudentRatingService studentRatingService;
    private final NotificationService notificationService;
    private final TaskService taskService;

    public AnswerService(AnswerRepository repository, MessageService messageService, FullAnswerMapper answerMapper, @Lazy StudentRatingService studentRatingService, NotificationService notificationService, @Lazy TaskService taskService) {
        super(repository);
        this.messageService = messageService;
        this.answerMapper = answerMapper;
        this.studentRatingService = studentRatingService;
        this.notificationService = notificationService;
        this.taskService = taskService;
    }

    public FullAnswerBean findFull(Long answerId, int level) {
        Answer answer = findById(answerId);
        checkNotNull(answer, "Answer not found");
        return answerMapper.map(answer, level);
    }

    public void generateAnswers(StudentRating studentRating) {
        ExamRule examRule = studentRating.getGroupRating().getExamRule();

        List<Task> questions = taskService.getQuestions(examRule);
        List<Task> exercises = taskService.getExercises(examRule);
        Set<Long> used = new HashSet<>();

        Integer exerciseDefaultRating = examRule.getSingleExerciseDefaultRating();
        Integer exerciseSum = examRule.getExercisesRatingSum();

        int counter = 1;
        int currentSum = 0;
        while (currentSum < exerciseSum) {
            Task task = exercises.get(random.nextInt(exercises.size()));
            while (used.contains(task.getId())) {
                task = exercises.get(random.nextInt(exercises.size()));
            }
            Answer answer = new Answer();
            answer.setStudentRating(studentRating);
            answer.setTask(task);
            answer.setState(AnswerState.NO_ANSWER);
            answer.setNumber(counter++);
            save(answer);

            used.add(task.getId());
            currentSum += exerciseDefaultRating;
        }

        Integer questionDefaultRating = examRule.getSingleQuestionDefaultRating();
        Integer questionSum = examRule.getQuestionsRatingSum();

        counter = 1;
        currentSum = 0;
        used.clear();
        while (currentSum < questionSum) {
            Task task = questions.get(random.nextInt(exercises.size()));
            while (used.contains(task.getId())) {
                task = questions.get(random.nextInt(exercises.size()));
            }
            Answer answer = new Answer();
            answer.setStudentRating(studentRating);
            answer.setTask(task);
            answer.setState(AnswerState.NO_ANSWER);
            answer.setNumber(counter++);
            save(answer);

            used.add(task.getId());
            currentSum += questionDefaultRating;
        }
    }

    public List<StudentAnswerBean> findByStudentRating(StudentRating studentRating, Pageable pageable) {
        return getRepository().findAllByStudentRating(studentRating, pageable).stream().map(this::mapToStudent).collect(Collectors.toList());
    }

    public Page<MessageBean> findAllMessages(Long answerId, Pageable pageable) {
        Answer answer = findById(answerId);
        checkNotNull(answer, "No answer found");
        return messageService.findAllByAnswer(answer, pageable);
    }

    public MessageBean newMessage(Long answerId, MessageBean messageBean, Account account) {
        Answer answer = findById(answerId);
        checkNotNull(answer, "Answer not found");
        Exam exam = answer.getStudentRating().getExam();
        checkTrue(ExamState.PROGRESS.equals(exam.getState()), "Wrong state");

        MessageBean answerMessage = messageService.createAnswerMessage(messageBean, answer, account);

        if (account.getRoles().contains(UserRole.ROLE_STUDENT) && IN_PROGRESS.equals(answer.getState())) {
            answer.setState(SENT);
        }

        if (account.getRoles().contains(UserRole.ROLE_TEACHER) && SENT.equals(answer.getState())) {
            answer.setState(CHECKING);
        }
        save(answer);
        return answerMessage;
    }

    @Override
    public Answer save(Answer entity) {
        Answer saved = super.save(entity);
        studentRatingService.answerStateChanged(saved);
        notificationService.answerStateChanged(saved);
        return saved;
    }

    public AnswerBean updateState(AnswerBean bean, Account account) {

        Answer answer = findById(bean.getId());
        checkNotNull(answer, "Answer not found");
        AnswerState newState = bean.getState();
        checkNotNull(newState, "State cannot be null");
        if (newState.equals(answer.getState())) {
            return map(answer);
        }
        checkTrue(newState.allowedFor(answer), "Wrong answer state");
        checkTrue(account.getRoles().contains(UserRole.ROLE_TEACHER) || NO_ANSWER.equals(answer.getState()),
                "Student cannot do this");

        if (RATED.equals(newState)) {
            return map(rateAnswer(answer, bean.getRating()));
        }

        answer.setState(newState);
        return map(save(answer));
    }

    private Answer rateAnswer(Answer answer, Integer rating) {
        StudentRating studentRating = answer.getStudentRating();
        checkNotNull(studentRating, "Student rating not found");

        Exam exam = studentRating.getExam();
        checkNotNull(exam, "Exam not found");

        checkTrue(exam.getState().in(ExamState.PROGRESS, ExamState.FINISHED), "Wrong exam state");
        ExamRule examRule = studentRating.getGroupRating().getExamRule();

        if (QUESTION.equals(answer.getTask().getTaskType())) {
            checkTrue(rating <= examRule.getSingleQuestionDefaultRating(),
                    "Rating cannot be bigger then max question rating");
        }
        if (EXERCISE.equals(answer.getTask().getTaskType())) {
            checkTrue(rating <= examRule.getSingleExerciseDefaultRating(),
                    "Rating cannot be bigger then max exercise rating");
        }

        answer.setRating(rating);
        answer.setState(RATED);
        Answer saved = save(answer);
        studentRatingService.answerStateChanged(saved);
        return saved;
    }

    @Override
    public void examClosed(Exam exam) {
        exam.getStudentRatings().stream()
                .map(StudentRating::getAnswers)
                .flatMap(Collection::stream)
                .filter(answer -> answer.getState().in(IN_PROGRESS, SENT, CHECKING))
                .forEach(answer -> {
                    answer.setState(NO_RATING);
                    save(answer);
                });
    }

    @Override
    public void delete(Answer answer) {
        for (Message message : CollectionUtils.emptyIfNull(answer.getMessages())) {
            messageService.delete(message);
        }
        super.delete(answer);
    }

    @Override
    protected AnswerBean map(Answer entity) {
        AnswerBean answerBean = new AnswerBean();
        answerBean.setId(entity.getId());
        answerBean.setStudentRatingId(entity.getStudentRating().getId());
        answerBean.setTaskId(entity.getTask().getId());
        answerBean.setState(entity.getState());
        answerBean.setNumber(entity.getNumber());
        answerBean.setRating(entity.getRating());
        return answerBean;
    }

    private StudentAnswerBean mapToStudent(Answer entity) {
        Task task = entity.getTask();

        StudentAnswerBean bean = new StudentAnswerBean();
        bean.setId(entity.getId());
        bean.setRating(entity.getRating());
        bean.setStudentRatingId(entity.getStudentRating() == null ? null : entity.getStudentRating().getId());
        bean.setNumber(entity.getNumber());
        bean.setState(entity.getState());

        StudentTaskBean taskBean = new StudentTaskBean();
        taskBean.setId(task.getId());
        taskBean.setTaskType(task.getTaskType());
        taskBean.setArtefactId(task.getArtefact() == null ? null : task.getArtefact().getId());
        taskBean.setText(task.getText());
        taskBean.setThemeName(task.getTheme() == null ? null : task.getTheme().getName());
        bean.setTask(taskBean);

        return bean;
    }

    public List<AnswerBean> getAnswersByStudentRating(StudentRating sr) {
        return mapToBeans(sr.getAnswers());
    }
}

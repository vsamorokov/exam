package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.StudentRatingBean;
import ru.nstu.exam.bean.full.FullStudentRatingBean;
import ru.nstu.exam.entity.*;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullStudentRatingMapper implements Mapper<FullStudentRatingBean, StudentRating> {

    private final FullAnswerMapper answerMapper;
    private final FullStudentMapper studentMapper;
    @Lazy
    private final FullExamMapper examMapper;
    @Lazy
    private final FullGroupRatingMapper groupRatingMapper;

    @Override
    public FullStudentRatingBean map(StudentRating entity, int level) {
        FullStudentRatingBean fullStudentRatingBean = new FullStudentRatingBean();

        if (level >= 0) {
            StudentRatingBean studentRatingBean = new StudentRatingBean();
            studentRatingBean.setId(entity.getId());
            studentRatingBean.setExerciseRating(entity.getExerciseRating());
            studentRatingBean.setQuestionRating(entity.getQuestionRating());
            studentRatingBean.setSemesterRating(entity.getSemesterRating());
            studentRatingBean.setStudentRatingState(entity.getStudentRatingState());
            studentRatingBean.setGroupRatingId(entity.getGroupRating().getId());
            studentRatingBean.setStudentId(entity.getStudent() == null ? null : entity.getStudent().getId());
            studentRatingBean.setExamId(entity.getExam() == null ? null : entity.getExam().getId());
            fullStudentRatingBean.setStudentRating(studentRatingBean);
        }
        if (level >= 1) {
            Collection<Answer> answers = CollectionUtils.emptyIfNull(entity.getAnswers());
            fullStudentRatingBean.setAnswers(
                    answers.stream()
                            .map(a -> answerMapper.map(a, level - 1))
                            .collect(Collectors.toList())
            );
            Student student = entity.getStudent();
            if (student != null) {
                fullStudentRatingBean.setStudent(studentMapper.map(student, level - 1));
            }
            Exam exam = entity.getExam();
            if (exam != null) {
                fullStudentRatingBean.setExam(examMapper.map(exam, level - 1));
            }
            GroupRating groupRating = entity.getGroupRating();
            if (groupRating != null) {
                fullStudentRatingBean.setGroupRating(groupRatingMapper.map(groupRating, level - 1));
            }
        }
        return fullStudentRatingBean;
    }
}

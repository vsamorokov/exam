package ru.nstu.exam.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.enums.AnswerState;
import ru.nstu.exam.repository.AnswerRepository;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;

public class AnswerServiceTest {

    @Test
    public void whenAnswerSavedThenStudentRatingServiceCalled() {
        AnswerRepository answerRepository = Mockito.mock(AnswerRepository.class);
        StudentRatingService studentRatingService = Mockito.mock(StudentRatingService.class);

        Answer answer = new Answer();
        answer.setState(AnswerState.IN_PROGRESS);

        AtomicReference<Answer> returned = new AtomicReference<>(null);
        Mockito.doAnswer(invocation -> {
            returned.set(invocation.getArgument(0));
            return null;
        }).when(studentRatingService).answerStateChanged(any());

        Mockito.doReturn(answer).when(answerRepository).save(any());

        AnswerService answerService = new AnswerService(
                answerRepository, null, null,
                studentRatingService, Collections.emptyList(), null);

        answerService.save(answer);

        Assertions.assertNotNull(returned.get());
        Assertions.assertEquals(AnswerState.IN_PROGRESS, returned.get().getState());
    }
}

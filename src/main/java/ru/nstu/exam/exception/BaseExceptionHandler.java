package ru.nstu.exam.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(value = {ExamException.class})
    protected ResponseEntity<String> handleExam(ExamException ex) {
        log.error("Exam exception occurred", ex);

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<String> handle(Exception ex) {
        log.error("Exception occurred", ex);

        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}

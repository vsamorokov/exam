package ru.nstu.exam.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ExamException extends RuntimeException {

    @Getter
    private final HttpStatus status;

    public ExamException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public static <T> T userError(String message) {
        throw new ExamException(message, HttpStatus.BAD_REQUEST);
    }

    public static <T> T serverError(String message) {
        throw new ExamException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

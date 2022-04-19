package ru.nstu.exam.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ExamException extends RuntimeException {

    @Getter
    private final HttpStatus status;

    public ExamException(String message, HttpStatus status) {
        this(message, status, null);
    }

    public ExamException(String message, HttpStatus status, Throwable throwable) {
        super(message, throwable);
        this.status = status;
    }

    public static <T> T userError(String message) {
        throw new ExamException(message, HttpStatus.BAD_REQUEST);
    }

    public static <T> T serverError(String message) {
        return serverError(message, null);
    }

    public static <T> T serverError(String message, Throwable throwable) {
        throw new ExamException(message, HttpStatus.INTERNAL_SERVER_ERROR, throwable);
    }
}

package com.ancas.reactive.ws.users.infrastructure.exception;

import com.ancas.reactive.ws.users.domain.exception.BadRequestException;
import com.ancas.reactive.ws.users.domain.exception.NotFoundException;
import com.ancas.reactive.ws.users.domain.exception.BadCredentialsException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

import static com.ancas.reactive.ws.users.domain.enums.Constants.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail customerNotFoundException(ConstraintViolationException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setTitle(DATA_ERROR.getValue());
        problemDetail.setType(URI.create("/data-error"));
        return problemDetail;
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail notFoundException(NotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setTitle(NOT_FOUND.getValue());
        problemDetail.setType(URI.create("/not-found"));
        return problemDetail;
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail badRequestException(BadRequestException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setTitle(BAD_REQUEST.getValue());
        problemDetail.setType(URI.create("/bad-request"));
        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail unauthenticatedException(BadCredentialsException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        problemDetail.setTitle(UNAUTHENTICATED.getValue());
        problemDetail.setType(URI.create("/unauthenticated"));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail internalServerErrorException(Exception exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problemDetail.setTitle(INTERNAL_SERVER_ERROR.getValue());
        problemDetail.setType(URI.create("/internal-server-error"));
        return problemDetail;
    }



}

package com.mps.project.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public ResponseMessage handle400(Exception e) {
        return new ResponseMessage(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseMessage handle404(ResourceNotFoundException e) {
        return new ResponseMessage(e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ResponseMessage handle409(ConflictException e) {
        return new ResponseMessage(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class, UsernameNotFoundException.class})
    public ResponseMessage handle401(RuntimeException e) {
        return new ResponseMessage(e.getMessage());
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(PasswordRegisterException.class)
    public ResponseMessage handle412(PasswordRegisterException e) {
        return new ResponseMessage(e.getMessage());
    }
}

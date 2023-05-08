package com.IBTim4.CertificatesApp.exceptions;

import lombok.*;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(value = { CustomExceptionWithMessage.class })
    protected ResponseEntity<ErrorMessage> handleCustomExceptionWithMessage(CustomExceptionWithMessage ex) {
        return new ResponseEntity<>(new ErrorMessage(ex.message), ex.httpStatus);
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    protected ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {

        String ret = "";

        for (ConstraintViolation violation : ex.getConstraintViolations()) {
            String fieldName = ((PathImpl) violation.getPropertyPath()).getLeafNode().toString();
            ret += "Constraint violation. Field (" + fieldName + ") format is not valid!\n";
        }

        return new ResponseEntity<>(ret, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    protected ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        String ret = "";

        for (ObjectError error : ex.getBindingResult().getAllErrors())
            ret += error.getDefaultMessage() + "\n";

        return new ResponseEntity<>(ret, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { MethodArgumentTypeMismatchException.class })
    protected ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {

        String fieldName = ex.getName();

        return new ResponseEntity<>("Wrong type. Field (" + fieldName + ") format is not valid!\n", HttpStatus.BAD_REQUEST);
    }

}


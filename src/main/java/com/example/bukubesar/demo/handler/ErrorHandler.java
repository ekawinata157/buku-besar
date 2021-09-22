package com.example.bukubesar.demo.handler;

import com.example.bukubesar.demo.model.ErrorHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorHolder> genericExceptionHandler(Exception ex) {
        ErrorHolder errorHolder = new ErrorHolder();
        errorHolder.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorHolder.setErrorMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorHolder);
    }
}

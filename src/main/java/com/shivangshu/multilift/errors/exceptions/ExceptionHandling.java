package com.shivangshu.multilift.errors.exceptions;

import com.shivangshu.multilift.errors.LiftNotFoundException;
import com.shivangshu.multilift.errors.UnknownLiftStatusError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(value = NullPointerException.class)
    protected ResponseEntity<Object> handleNullPointerException(NullPointerException e, WebRequest request) {
        return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = UnknownLiftStatusError.class)
    public ResponseEntity<String> handleLiftNotFoundException(UnknownLiftStatusError e, WebRequest request) {
        return new ResponseEntity<String>("Unknown Lift Status Request received",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InterruptedException.class)
    public ResponseEntity<String> handleInterruptedException(InterruptedException e, WebRequest request) {
        return new ResponseEntity<String>("Internal Server Error in handling request ", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = LiftNotFoundException.class)
    public ResponseEntity<String> handleLiftNotFoundException(LiftNotFoundException e, WebRequest request) {
        return new ResponseEntity<String>("Lift not found for desired request",HttpStatus.BAD_REQUEST);
    }
}

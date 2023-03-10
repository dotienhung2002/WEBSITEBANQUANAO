package com.application.fusamate.exception;

import com.application.fusamate.configuration.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler  {


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request){
        System.out.println("handleResponseStatusException....");
        System.out.println(ex.getMessage());
        System.out.println("handleResponseStatusException....");

        ApiErrorResponse  apiError = ApiErrorResponseBuilder.getInstance()
                .withStatus(ex.getStatus().value()).withError(ex.getMessage())
                .build();
        return new ResponseEntity<ApiErrorResponse>(apiError,ex.getStatus());
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request){
        System.out.println("handleConstraintViolationException....");
        ApiErrorResponse  apiError = ApiErrorResponseBuilder.getInstance()
                .withStatus(HttpStatus.BAD_REQUEST.value()).withError(ex.getMessage())
                .build();

        return new ResponseEntity<ApiErrorResponse>(apiError,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex , HttpServletRequest request){
        System.out.println("handleException...");

        ApiErrorResponse  apiError = ApiErrorResponseBuilder.getInstance()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()).withError(ex.getMessage())
                .build();
        if (ex.getMessage()=="INVALID_CREDENTIALS"){
            apiError.setError(Constants.USERNAME_OR_PASSWORD_NOT_MATCH);
        }
        return new ResponseEntity<ApiErrorResponse>(apiError,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest req) {
        System.out.println("handleNoHandlerFoundException...");

        HttpServletRequest request = ((ServletWebRequest) req).getRequest();
        ApiErrorResponse apiError = ApiErrorResponseBuilder.getInstance()
                .withStatus(status.value()).withError(ex.getMessage())
                .build();
        return new ResponseEntity<Object>(apiError, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        System.out.println("handleMethodArgumentNotValid...");
        System.out.println(ex.getMessage());
        HttpServletRequest req = ((ServletWebRequest)request).getRequest();
        ApiErrorResponse  apiError = ApiErrorResponseBuilder.getInstance()
                .withStatus(status.value()).withError(Constants.BAD_REQUEST)
                .build();
        return new ResponseEntity<Object>(apiError,headers,status);
    }
}

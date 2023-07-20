package com.revelvol.progressservice.controller.exceptionHandler;


import com.revelvol.progressservice.dto.ApiError;
import com.revelvol.progressservice.exception.ProgressNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ProgressNotFoundException.class})
    public ResponseEntity<ApiError> handleException(Exception e, WebRequest request) throws Throwable{
        HttpHeaders headers = new HttpHeaders();
        if (e instanceof MethodArgumentNotValidException subEx) {
            HttpStatus status = HttpStatus.BAD_REQUEST;

            return handleMethodArgumentNotValid(subEx, headers, status, request);
        } else if (e instanceof ProgressNotFoundException subEx) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleProgressNotFoundException(subEx, headers, status, request);
        }



        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(e, null, headers, status, request);




    }


    private ResponseEntity<ApiError> handleExceptionInternal(Exception e, ApiError body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if(status == HttpStatus.INTERNAL_SERVER_ERROR){
            //if the else statement of internal server error is reach, will response default api response
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, e, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body,headers,status);

    }

    private ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException subEx, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError body = new ApiError();
        body.setCode(status.value());
        body.setMessage("Invalid request context");
        List<String> errorMessages = new ArrayList<>();

        subEx.getAllErrors().forEach((error) -> {
            errorMessages.add(error.getDefaultMessage());
        });


        body.setErrorsDetails(errorMessages);

        return handleExceptionInternal(subEx, body, headers, status, request);

    }

    private ResponseEntity<ApiError> handleProgressNotFoundException(ProgressNotFoundException subEx, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError body = new ApiError();
        body.setCode(status.value());
        body.setMessage("Not found");
        body.setErrorsDetails(Collections.singletonList(subEx.getMessage()));

        return handleExceptionInternal(subEx, body, headers, status, request);

    }



}

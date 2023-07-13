package com.revelvol.equipmentservice.controller.exceptionHandler;

import com.revelvol.equipmentservice.dto.ApiError;
import com.revelvol.equipmentservice.exception.EquipmentNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EquipmentNotFoundException.class, MethodArgumentNotValidException.class, MissingServletRequestParameterException.class, HttpMessageNotReadableException.class})
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) throws Throwable {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof EquipmentNotFoundException subEx) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleEquipmentNotFoundException(subEx, headers, status, request);
        } else if (ex instanceof MethodArgumentNotValidException subEx) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleMethodArgumentNotValidException(subEx, headers, status, request);
        } else if (ex instanceof MissingServletRequestParameterException subEx) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleMissingServletRequestParameterException(subEx, headers, status, request);
        } else if (ex instanceof HttpMessageNotReadableException subEx) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleHttpMessageNotReadableException(subEx, headers, status, request);
        }


        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(ex, null, headers, status, request);
    }


    private ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpHeaders headers, HttpStatus status, WebRequest request) throws Throwable {
        final Throwable cause = e.getCause();
        ApiError apiError = new ApiError();
        apiError.setCode(status.value());
        apiError.setMessage("Invalid request body");

        List<String> errorsDetail = null;

    /*    if (cause instanceof JsonParseException) {
            errorsDetail= onParseException((JsonParseException) cause);
        } else if (cause instanceof JsonMappingException) {
            errorsDetail= onMappingException((JsonMappingException) cause);
        } else if (cause instanceof JsonProcessingException) {
            errorsDetail= onProcessingException((JsonProcessingException) cause);
        }
*/
        apiError.setErrorsDetails(errorsDetail);


        return handleExceptionInternal(e, apiError, headers, status, request);
    }

    private ResponseEntity<ApiError> handleMissingServletRequestParameterException(MissingServletRequestParameterException subEx, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = Collections.singletonList(subEx.getMessage());
        ApiError apiError = new ApiError();
        apiError.setCode(status.value());
        apiError.setErrorsDetails(errors);
        apiError.setMessage("Invalid request body");

        return handleExceptionInternal(subEx, apiError, headers, status, request);
    }

    private ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException subEx, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorMessages = new ArrayList<>();
        subEx.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorMessages.add(fieldName + " " + errorMessage);
        });

        ApiError apiError = new ApiError();
        apiError.setCode(status.value());
        apiError.setErrorsDetails(errorMessages);
        apiError.setMessage("Invalid request context ");
        return handleExceptionInternal(subEx, apiError, headers, status, request);
    }

    protected ResponseEntity<ApiError> handleEquipmentNotFoundException(EquipmentNotFoundException subEx, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = Collections.singletonList(subEx.getMessage());
        ApiError apiError = new ApiError();
        apiError.setCode(status.value());
        apiError.setErrorsDetails(errors);
        apiError.setMessage("Not Found");

        return handleExceptionInternal(subEx, apiError, headers, status, request);

    }

    private ResponseEntity<ApiError> handleExceptionInternal(Exception ex, ApiError body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            //if the else statement of internal server error is reach, will response default api response
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}
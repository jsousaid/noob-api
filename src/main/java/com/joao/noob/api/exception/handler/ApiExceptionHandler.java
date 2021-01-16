package com.joao.noob.api.exception.handler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.joao.noob.domain.exception.ServiceException;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String INVALID_FIELD_ERROR_MESSAGE = "Um ou mais campos estão inválidos. Tente novamente!";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        var responseModel = new ResponseModel();
        var fields = getErrorFields(ex);

        responseModel.setStatus(status.value());
        responseModel.setMessage(INVALID_FIELD_ERROR_MESSAGE);
        responseModel.setTime(OffsetDateTime.now());
        responseModel.setErrorFields(fields);

        return super.handleExceptionInternal(ex, responseModel, headers, status,
                request);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleServiceException(ServiceException e,
            WebRequest webRequest) {

        var responseModel = new ResponseModel();
        var status = HttpStatus.BAD_REQUEST;

        responseModel.setStatus(status.value());
        responseModel.setMessage(e.getLocalizedMessage());
        responseModel.setTime(OffsetDateTime.now());

        return handleExceptionInternal(e, responseModel, new HttpHeaders(),
                status, webRequest);

    }

    private List<ResponseModel.Field> getErrorFields(
            MethodArgumentNotValidException ex) {

        var fields = new ArrayList<ResponseModel.Field>();

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {

            String errorField = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            fields.add(new ResponseModel.Field(errorField, errorMessage));
        }

        return fields;
    }
}

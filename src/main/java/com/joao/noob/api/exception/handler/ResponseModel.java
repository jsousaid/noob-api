package com.joao.noob.api.exception.handler;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ResponseModel {

    private Integer status;
    private OffsetDateTime time;
    private String message;
    private List<Field> errorFields;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public void setTime(OffsetDateTime time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Field> getErrorFields() {
        return errorFields;
    }

    public void setErrorFields(List<Field> errorFields) {
        this.errorFields = errorFields;
    }

    public static class Field {

        private String fieldName;
        private String fieldError;

        public Field(String fieldName, String fieldError) {
            super();
            this.fieldName = fieldName;
            this.fieldError = fieldError;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldError() {
            return fieldError;
        }

        public void setFieldError(String fieldError) {
            this.fieldError = fieldError;
        }

    }

}

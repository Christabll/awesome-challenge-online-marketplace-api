package com.awesomity.marketplace.marketplace_api.exception;

import com.awesomity.marketplace.marketplace_api.entity.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final ErrorCode code;
    private final Object[] args;

    public ResourceNotFoundException(String resource, String fieldName, Object fieldValue) {
        super(String.format("%s with %s '%s' not found", resource, fieldName, fieldValue));
        this.code = ErrorCode.ENTITY_NOT_FOUND;
        this.args = new Object[]{resource, fieldName, fieldValue};
    }

    public ResourceNotFoundException(String message, Object... args) {
        super(message);
        this.code = ErrorCode.ENTITY_NOT_FOUND;
        this.args = args;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.code = ErrorCode.ENTITY_NOT_FOUND;
        this.args = new Object[]{};
    }
}

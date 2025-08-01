package com.api.devsync.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ExternalSystemException extends RuntimeException {
    public ExternalSystemException(String message) {
        super(message);
    }
}

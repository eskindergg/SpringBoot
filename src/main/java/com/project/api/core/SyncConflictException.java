package com.project.api.core;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SyncConflictException extends RuntimeException {
    public SyncConflictException(String msg) {
        super(msg);
    }
}

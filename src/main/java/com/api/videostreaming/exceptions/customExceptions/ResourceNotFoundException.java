package com.api.videostreaming.exceptions.customExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Sets HTTP status to 404
public class ResourceNotFoundException extends RuntimeException { 
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

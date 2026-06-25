package com.company.identitymanager.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
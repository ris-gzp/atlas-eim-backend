package com.company.identitymanager.cognito;

public class CognitoException extends RuntimeException {

    public CognitoException(String message) {
        super(message);
    }

    public CognitoException(String message, Throwable cause) {
        super(message, cause);
    }
}
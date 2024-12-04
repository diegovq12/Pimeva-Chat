package com.pimeva.pimevachat.exceptions;

public class ChatNotFoundException extends Exception{
    public ChatNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}

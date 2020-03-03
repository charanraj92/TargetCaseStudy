package com.retail.retailAPI.exceptions;

public class ServerException extends RuntimeException {

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Object... args) {
        super(String.format(message, args));
    }
}

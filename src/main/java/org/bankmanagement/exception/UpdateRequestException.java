package org.bankmanagement.exception;

public class UpdateRequestException extends RuntimeException {
    public UpdateRequestException() {
        super("Your update request has no impact.");
    }
}

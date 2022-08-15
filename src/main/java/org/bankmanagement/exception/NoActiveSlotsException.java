package org.bankmanagement.exception;

public class NoActiveSlotsException extends RuntimeException {
    public NoActiveSlotsException(String username) {
        super(String.format("No active slots found for user %s", username));
    }
}

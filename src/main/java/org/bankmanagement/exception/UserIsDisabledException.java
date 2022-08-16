package org.bankmanagement.exception;

public class UserIsDisabledException extends RuntimeException {
    public UserIsDisabledException(String username) {
        super("User is disabled by username " + username);
    }

    public UserIsDisabledException(Long clientId) {
        super("User is disabled by id " + clientId);
    }
}

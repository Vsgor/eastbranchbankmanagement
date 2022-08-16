package org.bankmanagement.exception;

public class UserAlreadyExistsByUsernameException extends RuntimeException {
    public UserAlreadyExistsByUsernameException(String username) {
        super("User already exists by username " + username);
    }
}

package org.bankmanagement.exceptions;

public class UserAlreadyExistsByUsernameException extends RuntimeException {
    public UserAlreadyExistsByUsernameException(String username) {
        super("User already exists by username: " + username);
    }
}

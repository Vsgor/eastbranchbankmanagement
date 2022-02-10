package org.bankmanagement.exceptions;

public class UserAlreadyExistsByUsername extends RuntimeException {
    public UserAlreadyExistsByUsername(String username) {
        super("User already exists by username: " + username);
    }
}

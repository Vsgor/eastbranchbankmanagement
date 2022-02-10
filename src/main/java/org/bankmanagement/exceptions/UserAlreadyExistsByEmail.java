package org.bankmanagement.exceptions;

public class UserAlreadyExistsByEmail extends RuntimeException {
    public UserAlreadyExistsByEmail(String email) {
        super("User already registered by eMail: " + email);
    }
}

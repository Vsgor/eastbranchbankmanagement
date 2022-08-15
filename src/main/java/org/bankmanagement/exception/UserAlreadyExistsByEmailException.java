package org.bankmanagement.exception;

public class UserAlreadyExistsByEmailException extends RuntimeException {
    public UserAlreadyExistsByEmailException(String email) {
        super("User already registered by eMail " + email);
    }
}

package org.bankmanagement.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException {
    public UserNotFoundException(String username) {
        super("User not found by name " + username);
    }

    public UserNotFoundException(Long clientId) {
        super("User not found by id " + clientId);
    }
}

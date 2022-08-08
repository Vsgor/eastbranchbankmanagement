package org.bankmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UpdateRequestException extends RuntimeException {
    public UpdateRequestException() {
        super("Your update request has no impact.");
    }
}

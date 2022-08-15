package org.bankmanagement.exception;

public class SlotNotFoundException extends RuntimeException {
    public SlotNotFoundException(Long slotId, String username) {
        super(String.format("Slot has not been found by id %d for user %s", slotId, username));
    }
}

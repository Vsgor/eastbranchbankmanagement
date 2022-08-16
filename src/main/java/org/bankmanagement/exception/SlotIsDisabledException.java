package org.bankmanagement.exception;

public class SlotIsDisabledException extends RuntimeException {
    public SlotIsDisabledException(Long slotId, String username) {
        super(String.format("Slot is disabled by id %d for user %s", slotId, username));
    }
}

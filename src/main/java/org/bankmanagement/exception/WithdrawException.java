package org.bankmanagement.exception;

public class WithdrawException extends RuntimeException {
    public WithdrawException(Long id, long state, Long transferSum) {
        super(String.format("Slot by id %d has state %d trying to withdraw sum %d", id, state, transferSum));
    }
}

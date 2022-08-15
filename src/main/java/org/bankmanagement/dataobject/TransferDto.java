package org.bankmanagement.dataobject;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class TransferDto {

    private Long withdrawSlotId;

    @NotNull
    private Long depositClientId;

    @NotNull
    @Positive
    private Long transferSum;
}

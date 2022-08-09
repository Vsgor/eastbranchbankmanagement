package org.bankmanagement.dataobject;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SlotDto {
    private Long id;
    private long state;
    private boolean active;
}

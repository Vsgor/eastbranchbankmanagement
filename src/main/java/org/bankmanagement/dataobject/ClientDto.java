package org.bankmanagement.dataobject;

import lombok.Getter;
import lombok.Setter;
import org.bankmanagement.enums.Role;

import java.util.List;

@Getter
@Setter
public class ClientDto {
    private Long id;
    private String email;
    private String username;
    private Role role;
    private boolean active;
    private List<SlotDto> slots;
}

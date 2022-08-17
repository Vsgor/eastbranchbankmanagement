package org.bankmanagement.dataobject;

import lombok.Getter;
import lombok.Setter;
import org.bankmanagement.enums.Role;

@Getter
@Setter
public class ClientDto {
    private Long id;
    private String email;
    private String username;
    private Role role;
    private boolean active;
}

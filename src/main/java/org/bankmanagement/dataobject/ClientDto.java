package org.bankmanagement.dataobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ClientDto {
    private Long id;
    private String email;
    private String username;
    private String role;
    private boolean active;
}

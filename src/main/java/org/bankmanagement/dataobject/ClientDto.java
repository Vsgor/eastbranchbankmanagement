package org.bankmanagement.dataobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
public class ClientDto {
    private long id;
    private String email;
    private String username;
    private String role;
    private boolean active;
}

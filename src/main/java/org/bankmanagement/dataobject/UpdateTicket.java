package org.bankmanagement.dataobject;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateTicket {
    @Email
    private String email;
    @Size(min = 3, max = 50)
    private String username;
    @Size(min = 3, max = 50)
    private String password;

}

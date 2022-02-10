package org.bankmanagement.data_transfer_objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Accessors(chain = true)
public class RegisterTicket {
    @NotNull
    @Size(min = 3, max = 50)
    private String username;
    @NotNull
    @Size(min = 4, max = 50)
    private String password;
    @Email
    @NotNull
    private String email;
}

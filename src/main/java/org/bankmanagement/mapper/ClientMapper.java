package org.bankmanagement.mapper;

import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.UserDetailsImpl;
import org.bankmanagement.entity.Client;
import org.bankmanagement.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    @Mapping(target = "authorities", ignore = true)
    UserDetailsImpl mapToUserDetails(Client client);

    ClientDto mapToDto(Client client);

    @Mappings({
            @Mapping(target = "active", constant = "true"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "slots", ignore = true)
    })
    Client mapToClient(String email, String username, String password, Role role);
}

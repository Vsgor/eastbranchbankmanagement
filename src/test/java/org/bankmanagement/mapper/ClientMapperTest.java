package org.bankmanagement.mapper;

import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.UserDetailsImpl;
import org.bankmanagement.entity.Client;
import org.bankmanagement.enums.Role;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ClientMapperTest {

    private final ClientMapper mapper = Mappers.getMapper(ClientMapper.class);

    @Test
    void testMapToDto() {
        Client client = new Client();
        client.setId(1L);
        client.setUsername("ussrname");
        client.setEmail("allert@red.ussr");
        client.setRole(Role.ROLE_CLIENT);
        client.setActive(true);

        ClientDto clientDto = mapper.mapToDto(client);

        assertAll(
                () -> assertThat(clientDto.getId()).isEqualTo(client.getId()),
                () -> assertThat(clientDto.getUsername()).isEqualTo(client.getUsername()),
                () -> assertThat(clientDto.getEmail()).isEqualTo(client.getEmail()),
                () -> assertThat(clientDto.getRole()).isEqualTo(client.getRole().name()),
                () -> assertThat(clientDto.isActive()).isEqualTo(client.isActive())
        );
    }

    @Test
    void testMapToClient() {
        String email = "mail@foo.bar";
        String username = "susermane";
        String password = "some encrypted password";
        Role role = Role.ROLE_ADMIN;

        Client client = mapper.mapToClient(email, username, password, role);

        assertAll(
                () -> assertThat(client.getId()).isNull(),
                () -> assertThat(client.getSlots()).isNull(),
                () -> assertThat(client.isActive()).isTrue(),
                () -> assertThat(client.getEmail()).isEqualTo(email),
                () -> assertThat(client.getUsername()).isEqualTo(username),
                () -> assertThat(client.getPassword()).isEqualTo(password),
                () -> assertThat(client.getRole()).isEqualTo(role)
        );
    }

    @Test
    void testMapToUserDetails() {
        Client client = new Client();
        client.setUsername("username");
        client.setPassword("password");
        client.setActive(false);
        client.setRole(Role.ROLE_ADMIN);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(client.getRole().name()));

        UserDetailsImpl userDetails = mapper.mapToUserDetails(client);

        assertAll(
                () -> assertThat(userDetails.getAuthorities()).isEqualTo(authorities),
                () -> assertThat(userDetails.getPassword()).isEqualTo(client.getPassword()),
                () -> assertThat(userDetails.getUsername()).isEqualTo(client.getUsername()),
                () -> assertThat(userDetails.isEnabled()).isEqualTo(client.isActive())
        );
    }

}
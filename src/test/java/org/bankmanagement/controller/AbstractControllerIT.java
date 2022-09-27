package org.bankmanagement.controller;

import lombok.SneakyThrows;
import org.bankmanagement.configuration.TestWebConfiguration;
import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.enums.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
@Import(TestWebConfiguration.class)
public abstract class AbstractControllerIT {

    protected static final String USERNAME = "Some random username";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;

    @SneakyThrows
    protected String mapToJson(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @NotNull
    protected ClientDto getClientDto(Long id) {
        ClientDto clientDto = new ClientDto();
        clientDto.setId(id);
        clientDto.setUsername(USERNAME);
        clientDto.setRole(Role.ROLE_CLIENT);
        clientDto.setActive(true);
        clientDto.setEmail("SDLfjhnoiubns");
        return clientDto;
    }
}

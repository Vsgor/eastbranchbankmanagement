package org.bankmanagement.controller;

import lombok.SneakyThrows;
import org.bankmanagement.configuration.TestWebConfiguration;
import org.springframework.context.annotation.Import;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@Import(TestWebConfiguration.class)
public class AbstractControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    protected String mapToJson(Object object) {
        return objectMapper.writeValueAsString(object);
    }
}

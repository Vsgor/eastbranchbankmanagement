package org.bankmanagement.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.bankmanagement.configuration.ResponseResultMatcher.responseBody;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerIT extends AbstractControllerIT {

    private static final String URI = "/admin";

    @Test
    @SneakyThrows
    @WithMockUser
    void helloWorld_WithNotAdmin() {
        mockMvc.perform(get(URI))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    void helloWorld_WithAdmin() {
        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(responseBody().messageStartsWith("Hello world"));
    }

}
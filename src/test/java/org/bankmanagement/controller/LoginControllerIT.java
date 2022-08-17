package org.bankmanagement.controller;

import lombok.SneakyThrows;
import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.RegisterTicket;
import org.bankmanagement.service.ClientService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bankmanagement.configuration.ResponseResultMatcher.responseBody;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
class LoginControllerIT extends AbstractControllerIT {

    public static final String URI = "/login/sign-up";
    @MockBean
    private ClientService clientService;

    @Captor
    private ArgumentCaptor<RegisterTicket> ticketCaptor;

    @Test
    @SneakyThrows
    void registerClient_WithEmptyTicket() {
        RegisterTicket regTicket = new RegisterTicket();

        mockMvc.perform(post(URI).contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(regTicket)))
                .andExpect(status().isBadRequest())
                .andExpect(responseBody().messageStartsWith("Validation failed"));

        verifyNoInteractions(clientService);
    }

    @Test
    @SneakyThrows
    void registerClient_WithInvalidTicket() {
        RegisterTicket regTicket = new RegisterTicket();
        regTicket.setEmail("not email");
        regTicket.setUsername("st");
        regTicket.setPassword("sht");

        mockMvc.perform(post(URI).contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(regTicket)))
                .andExpect(status().isBadRequest())
                .andExpect(responseBody().messageStartsWith("Validation failed"));

        verifyNoInteractions(clientService);
    }

    @Test
    @SneakyThrows
    void registerClient_WithValidTicket() {
        ClientDto clientDto = getClientDto(33L);

        RegisterTicket regTicket = new RegisterTicket();
        regTicket.setEmail("valid@mail.ru");
        regTicket.setUsername("at least 3");
        regTicket.setPassword("at least 4");

        when(clientService.registerClient(any(RegisterTicket.class))).thenReturn(clientDto);

        mockMvc.perform(post(URI).contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(regTicket)))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsObject(clientDto, ClientDto.class));

        verify(clientService).registerClient(ticketCaptor.capture());

        assertThat(ticketCaptor.getValue()).usingRecursiveComparison().isEqualTo(regTicket);
    }

}
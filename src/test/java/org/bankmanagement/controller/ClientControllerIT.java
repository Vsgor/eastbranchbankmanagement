package org.bankmanagement.controller;

import lombok.SneakyThrows;
import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.UpdateTicket;
import org.bankmanagement.service.ClientService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bankmanagement.configuration.ResponseResultMatcher.responseBody;
import static org.bankmanagement.controller.AbstractControllerIT.USERNAME;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@WithMockUser(username = USERNAME)
class ClientControllerIT extends AbstractControllerIT {

    private static final String URI = "/client";

    @MockBean
    private ClientService clientService;
    @Captor
    private ArgumentCaptor<UpdateTicket> ticketCaptor;

    @Test
    @SneakyThrows
    void getClientInfo() {
        ClientDto clientDto = getClientDto(52L);

        when(clientService.getClient(USERNAME)).thenReturn(clientDto);

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsObject(clientDto, ClientDto.class));

        verify(clientService).getClient(USERNAME);
    }

    @Test
    @SneakyThrows
    void updateClientInfo_WithInvalidTicket() {
        UpdateTicket ticket = new UpdateTicket();
        ticket.setEmail("not email");
        ticket.setUsername("st");
        ticket.setPassword("sh");

        mockMvc.perform(put(URI).contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(ticket)))
                .andExpect(status().isBadRequest())
                .andExpect(responseBody().messageStartsWith("Validation failed"));

        verifyNoInteractions(clientService);
    }

    @Test
    @SneakyThrows
    void updateClientInfo_WithEmptyTicket() {
        ClientDto clientDto = getClientDto(22L);
        UpdateTicket ticket = new UpdateTicket();

        when(clientService.updateClient(eq(USERNAME), any(UpdateTicket.class))).thenReturn(clientDto);

        mockMvc.perform(put(URI).contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(ticket)))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsObject(clientDto, ClientDto.class));

        verify(clientService).updateClient(eq(USERNAME), ticketCaptor.capture());

        assertThat(ticketCaptor.getValue()).usingRecursiveComparison().isEqualTo(ticket);
    }

    @Test
    @SneakyThrows
    void updateClientInfo() {
        ClientDto clientDto = getClientDto(22L);
        UpdateTicket ticket = new UpdateTicket();
        ticket.setEmail("valid@mail.ru");
        ticket.setUsername("3 chars at least");
        ticket.setPassword("50 at max");

        when(clientService.updateClient(eq(USERNAME), any(UpdateTicket.class))).thenReturn(clientDto);

        mockMvc.perform(put(URI).contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(ticket)))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsObject(clientDto, ClientDto.class));

        verify(clientService).updateClient(eq(USERNAME), ticketCaptor.capture());

        assertThat(ticketCaptor.getValue()).usingRecursiveComparison().isEqualTo(ticket);
    }

}
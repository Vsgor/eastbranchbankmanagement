package org.bankmanagement.service.clientservice;

import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.entity.Client;
import org.bankmanagement.exception.*;
import org.bankmanagement.mapper.ClientMapper;
import org.bankmanagement.repository.ClientRepository;
import org.bankmanagement.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserTest {

    @Mock
    private ClientMapper clientMapper;
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService service;

    @Test
    public void getClientShouldThrowUserNotFoundException() {
        String username = "mock username";
        when(clientRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getClient(username));

        verify(clientRepository).findByUsername(any());
        verifyNoInteractions(clientMapper);
    }

    @Test
    public void getClientShouldThrowUserIsDisabledException() {
        String username = "Tutor";
        Client client = new Client();
        client.setUsername(username);
        client.setActive(false);

        when(clientRepository.findByUsername(username)).thenReturn(Optional.of(client));

        assertThrows(UserIsDisabledException.class, () -> service.getClient(username));

        verify(clientRepository).findByUsername(username);
        verifyNoInteractions(clientMapper);
    }

    @Test
    public void getClientShouldSucceed() {
        String username = "Tutor";
        Client client = new Client();
        client.setUsername(username);
        client.setActive(true);
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername(username);
        clientDto.setActive(true);

        when(clientRepository.findByUsername(username)).thenReturn(Optional.of(client));
        when(clientMapper.mapToDto(client)).thenReturn(clientDto);

        ClientDto response = service.getClient(username);

        assertEquals(clientDto, response);

        verify(clientRepository).findByUsername(username);
        verify(clientMapper).mapToDto(client);
    }
}
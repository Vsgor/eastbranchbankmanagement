package org.bankmanagement.service.clientservice;

import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.UpdateTicket;
import org.bankmanagement.entity.Client;
import org.bankmanagement.exception.UserAlreadyExistsByEmailException;
import org.bankmanagement.exception.UserAlreadyExistsByUsernameException;
import org.bankmanagement.exception.UserIsDisabledException;
import org.bankmanagement.exception.UserNotFoundException;
import org.bankmanagement.mapper.ClientMapper;
import org.bankmanagement.repository.ClientRepository;
import org.bankmanagement.service.ClientService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateClientTest {

    @Mock
    private ClientMapper clientMapper;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private ClientService service;

    @Test
    public void updateClientShouldThrowUserNotFoundException() {
        when(clientRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.updateClient(any(), new UpdateTicket()));

        verify(clientRepository, only()).findByUsername(any());
        verifyNoInteractions(clientMapper);
        verifyNoInteractions(encoder);
    }

    @Test
    public void updateClientShouldThrowUserIsDisabledException() {
        Client client = new Client();
        client.setActive(false);
        when(clientRepository.findByUsername(any())).thenReturn(Optional.of(client));

        assertThrows(UserIsDisabledException.class, () -> service.updateClient(any(), new UpdateTicket()));

        verify(clientRepository, only()).findByUsername(any());
        verifyNoInteractions(clientMapper);
        verifyNoInteractions(encoder);
    }

    @NotNull
    private static UpdateTicket getUpdateTicket(String email, String username, String password) {
        UpdateTicket ticket = new UpdateTicket();
        ticket.setUsername(username);
        ticket.setPassword(password);
        ticket.setEmail(email);
        return ticket;
    }

    @NotNull
    private static Client getClient(String email, String username, String password) {
        Client client = new Client();
        client.setUsername(username);
        client.setActive(true);
        client.setEmail(email);
        client.setPassword(password);
        return client;
    }

    @Test
    public void updateClient_ShouldThrowUserAlreadyExistsByEmailException() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";
        String newEmail = "bla@bla.bla";

        Client client = getClient(email, username, password);
        UpdateTicket ticket = getUpdateTicket(newEmail, null, null);

        when(clientRepository.findByUsername(username)).thenReturn(Optional.of(client));
        when(clientRepository.existsByEmail(newEmail)).thenReturn(true);

        assertThrows(UserAlreadyExistsByEmailException.class, () -> service.updateClient(username, ticket));

        verify(clientRepository).findByUsername(username);
        verify(clientRepository).existsByEmail(newEmail);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(clientMapper);
        verifyNoInteractions(encoder);
    }

    @Test
    public void updateClientShouldThrowUserAlreadyExistsByUsernameException() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";
        String newUsername = "blablalba";

        Client client = getClient(email, username, password);
        UpdateTicket ticket = getUpdateTicket(null, newUsername, null);

        when(clientRepository.findByUsername(username)).thenReturn(Optional.of(client));
        when(clientRepository.existsByUsername(newUsername)).thenReturn(true);

        assertThrows(UserAlreadyExistsByUsernameException.class, () -> service.updateClient(username, ticket));

        verify(clientRepository).findByUsername(username);
        verify(clientRepository).existsByUsername(newUsername);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(clientMapper);
        verifyNoInteractions(encoder);
    }

    @Test
    public void updateClientShouldSucceed() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";

        String newEmail = "immortal@legend.heaven";
        String newName = "Paparapa";
        String newPassword = "Poow";

        Client client = getClient(email, username, password);
        UpdateTicket ticket = getUpdateTicket(newEmail, newName, newPassword);
        ClientDto dto = new ClientDto();

        when(clientRepository.findByUsername(username)).thenReturn(Optional.of(client));
        when(clientRepository.existsByEmail(newEmail)).thenReturn(false);
        when(clientRepository.existsByUsername(newName)).thenReturn(false);
        when(encoder.encode(newPassword)).thenReturn(newPassword);
        when(clientMapper.mapToDto(client)).thenReturn(dto);

        ClientDto response = service.updateClient(username, ticket);

        assertEquals(dto, response);

        verify(clientRepository).findByUsername(username);
        verify(clientRepository).existsByUsername(newName);
        verify(clientRepository).existsByEmail(newEmail);
        verify(clientMapper).mapToDto(client);
        verify(encoder).matches(newPassword, password);
        verify(encoder).encode(newPassword);
    }

}
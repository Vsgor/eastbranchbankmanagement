package org.bankmanagement.service;

import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.RegisterTicket;
import org.bankmanagement.dataobject.UpdateTicket;
import org.bankmanagement.entity.Client;
import org.bankmanagement.enums.Role;
import org.bankmanagement.exception.UserAlreadyExistsByEmailException;
import org.bankmanagement.exception.UserAlreadyExistsByUsernameException;
import org.bankmanagement.exception.UserIsDisabledException;
import org.bankmanagement.exception.UserNotFoundException;
import org.bankmanagement.mapper.ClientMapper;
import org.bankmanagement.repository.ClientRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientMapper clientMapper;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PasswordEncoder encoder;

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
    public void regUserShouldThrowUserAlreadyExistsByEmailException() {
        when(clientRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(UserAlreadyExistsByEmailException.class, () -> service.registerClient(new RegisterTicket()));

        verify(clientRepository, only()).existsByEmail(any());
        verifyNoInteractions(encoder);
        verifyNoInteractions(clientMapper);
    }

    @Test
    public void regUserShouldThrowUserAlreadyExistsByUsernameException() {
        when(clientRepository.existsByEmail(any())).thenReturn(false);
        when(clientRepository.existsByUsername(any())).thenReturn(true);

        assertThrows(UserAlreadyExistsByUsernameException.class, () -> service.registerClient(new RegisterTicket()));

        verify(clientRepository).existsByEmail(any());
        verify(clientRepository).existsByUsername(any());
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(encoder);
        verifyNoInteractions(clientMapper);
    }

    @Test
    public void regUserShouldSucceed() {
        String email = "test@tutor.org";
        String username = "Tutor";
        String password = "Rail";

        RegisterTicket ticket = new RegisterTicket();
        ticket.setEmail(email);
        ticket.setUsername(username);
        ticket.setPassword(password);
        Client client = new Client();
        client.setEmail(email);
        client.setUsername(username);
        client.setPassword(password);
        client.setRole(Role.ROLE_CLIENT);
        client.setActive(true);
        ClientDto dto = new ClientDto();

        when(clientRepository.existsByEmail(email)).thenReturn(false);
        when(clientRepository.existsByUsername(username)).thenReturn(false);
        when(encoder.encode(password)).thenReturn(password);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.mapToClient(email, username, password, Role.ROLE_CLIENT)).thenReturn(client);
        when(clientMapper.mapToDto(client)).thenReturn(dto);

        ClientDto response = service.registerClient(ticket);
        assertEquals(dto, response);

        ArgumentCaptor<Client> userArgumentCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(userArgumentCaptor.capture());
        assertEquals(client, userArgumentCaptor.getValue());

        verify(clientRepository).existsByEmail(any());
        verify(clientRepository).existsByUsername(any());
        verify(encoder).encode(password);
        verify(clientMapper).mapToDto(client);
    }

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
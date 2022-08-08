package org.bankmanagement.service.clientservice;

import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.UpdateTicket;
import org.bankmanagement.entity.Client;
import org.bankmanagement.exception.*;
import org.bankmanagement.mapper.ClientMapper;
import org.bankmanagement.repository.ClientRepository;
import org.bankmanagement.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void updateClient_ShouldThrowUserAlreadyExistsByEmailException() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";
        Client client = new Client();
        client.setUsername(username);
        client.setActive(true);
        client.setEmail(email);
        client.setPassword(password);
        UpdateTicket ticket = new UpdateTicket();
        String newEmail = "bla@bla.bla";
        ticket.setEmail(newEmail);

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
        Client client = new Client();
        client.setUsername(username);
        client.setActive(true);
        client.setEmail(email);
        client.setPassword(password);
        UpdateTicket ticket = new UpdateTicket();
        String newUsername = "blablalba";
        ticket.setUsername(newUsername);

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
    public void updateClient_WithEmptyTicket_ShouldThrowUpdateRequestException() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";
        Client client = new Client();
        client.setUsername(username);
        client.setActive(true);
        client.setEmail(email);
        client.setPassword(password);

        when(clientRepository.findByUsername(username)).thenReturn(Optional.of(client));

        assertThrows(UpdateRequestException.class, () -> service.updateClient(username, new UpdateTicket()));

        verify(clientRepository).findByUsername(username);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(encoder);
        verifyNoInteractions(clientMapper);
    }

    @Test
    public void updateClient_WithSameData_ShouldThrowUpdateRequestException() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";
        Client client = new Client();
        client.setUsername(username);
        client.setActive(true);
        client.setEmail(email);
        client.setPassword(password);
        UpdateTicket ticket = new UpdateTicket().setUsername(username).setPassword(password).setEmail(email);

        when(clientRepository.findByUsername(username)).thenReturn(Optional.of(client));
        when(encoder.matches(password, password)).thenReturn(true);

        assertThrows(UpdateRequestException.class, () -> service.updateClient(username, ticket));

        verify(clientRepository).findByUsername(username);
        verify(encoder, only()).matches(password, password);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(clientMapper);
    }

    @Test
    public void updateClientShouldSucceed() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";

        String newEmail = "immortal@legend.heaven";
        String newName = "Paparapa";
        String newPassword = "Poow";

        Client client = new Client();
        client.setUsername(username);
        client.setActive(true);
        client.setEmail(email);
        client.setPassword(password);
        UpdateTicket ticket = new UpdateTicket().setUsername(newName).setPassword(newPassword).setEmail(newEmail);
        ClientDto dto = new ClientDto();

        when(clientRepository.findByUsername(username)).thenReturn(Optional.of(client));
        when(clientRepository.existsByEmail(newEmail)).thenReturn(false);
        when(clientRepository.existsByUsername(newName)).thenReturn(false);
        when(encoder.encode(newPassword)).thenReturn(newPassword);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.mapToDto(client)).thenReturn(dto);

        ClientDto response = service.updateClient(username, ticket);

        assertEquals(dto, response);

        ArgumentCaptor<Client> userArgumentCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(userArgumentCaptor.capture());
        Client value = userArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(newEmail, value.getEmail()),
                () -> assertEquals(newName, value.getUsername()),
                () -> assertEquals(newPassword, value.getPassword())
        );

        verify(clientRepository).findByUsername(username);
        verify(clientRepository).existsByUsername(newName);
        verify(clientRepository).existsByEmail(newEmail);
        verify(clientMapper).mapToDto(client);
        verify(encoder).matches(newPassword, password);
        verify(encoder).encode(newPassword);
    }

}
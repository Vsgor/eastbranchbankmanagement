package org.bankmanagement.service.clientservice;

import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.RegisterTicket;
import org.bankmanagement.entity.Client;
import org.bankmanagement.enums.Role;
import org.bankmanagement.exception.UserAlreadyExistsByEmailException;
import org.bankmanagement.exception.UserAlreadyExistsByUsernameException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterClientTest {

    @Mock
    private ClientMapper clientMapper;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private ClientService service;

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

        RegisterTicket ticket = new RegisterTicket().setEmail(email).setUsername(username).setPassword(password);
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


}
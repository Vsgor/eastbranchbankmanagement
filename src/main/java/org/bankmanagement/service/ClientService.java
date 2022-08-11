package org.bankmanagement.service;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.aspect.log.LogMethod;
import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.RegisterTicket;
import org.bankmanagement.dataobject.UpdateTicket;
import org.bankmanagement.entity.Client;
import org.bankmanagement.enums.Role;
import org.bankmanagement.exception.*;
import org.bankmanagement.mapper.ClientMapper;
import org.bankmanagement.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientMapper clientMapper;
    private final ClientRepository userRepo;
    private final PasswordEncoder encoder;

    @LogMethod
    public ClientDto getClient(String username) {
        Client client = findClient(username);
        return clientMapper.mapToDto(client);
    }

    @LogMethod
    @Transactional
    public ClientDto updateClient(String username, UpdateTicket ticket) {
        Client client = findClient(username);

        String newEmail = ticket.getEmail();
        String newUsername = ticket.getUsername();
        String newPassword = ticket.getPassword();
        boolean changed = false;

        if (isNotBlank(newEmail) && !newEmail.equals(client.getEmail())) {
            if (userRepo.existsByEmail(newEmail)) throw new UserAlreadyExistsByEmailException(newEmail);
            client.setEmail(newEmail);
            changed = true;
        }
        if (isNotBlank(newUsername) && !newUsername.equals(client.getUsername())) {
            if (userRepo.existsByUsername(newUsername)) throw new UserAlreadyExistsByUsernameException(newUsername);
            client.setUsername(newUsername);
            changed = true;
        }
        if (isNotBlank(newPassword) && !encoder.matches(newPassword, client.getPassword())) {
            client.setPassword(encoder.encode(newPassword));
            changed = true;
        }

        if (changed) return clientMapper.mapToDto(userRepo.save(client));

        throw new UpdateRequestException();
    }

    @LogMethod
    public ClientDto registerClient(RegisterTicket ticket) {
        String email = ticket.getEmail();
        String username = ticket.getUsername();

        if (userRepo.existsByEmail(email)) throw new UserAlreadyExistsByEmailException(email);
        if (userRepo.existsByUsername(username)) throw new UserAlreadyExistsByUsernameException(username);

        String encodedPassword = encoder.encode(ticket.getPassword());
        Client client = clientMapper.mapToClient(email, username, encodedPassword, Role.ROLE_CLIENT);

        return clientMapper.mapToDto(userRepo.save(client));
    }

    protected Client findClient(String username) {
        Client client = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        if (!client.isActive()) throw new UserIsDisabledException(username);
        return client;
    }
}

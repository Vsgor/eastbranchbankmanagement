package org.bankmanagement.service;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.RegisterTicket;
import org.bankmanagement.dataobject.UpdateTicket;
import org.bankmanagement.entity.Client;
import org.bankmanagement.entity.Role;
import org.bankmanagement.exception.*;
import org.bankmanagement.mapper.ClientMapper;
import org.bankmanagement.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientMapper clientMapper;
    private final ClientRepository userRepo;
    private final PasswordEncoder encoder;

    public ClientDto regUser(RegisterTicket ticket) {
        String email = ticket.getEmail();
        String username = ticket.getUsername();

        if (userRepo.existsByEmail(email)) throw new UserAlreadyExistsByEmailException(email);
        if (userRepo.existsByUsername(username)) throw new UserAlreadyExistsByUsernameException(username);

        String encodedPassword = encoder.encode(ticket.getPassword());
        Client client = clientMapper.mapToClient(email, username, encodedPassword, Role.ROLE_CLIENT);

        return clientMapper.mapToDto(userRepo.save(client));
    }

    public ClientDto getClient(String username) {
        Client client = findUser(username);
        return clientMapper.mapToDto(client);
    }

    public ClientDto updateClient(String username, UpdateTicket ticket) {
        Client client = findUser(username);
        boolean changed = false;

        String newEmail = client.getEmail().equals(ticket.getEmail()) ? null : ticket.getEmail();
        String newName = client.getUsername().equals(ticket.getUsername()) ? null : ticket.getUsername();

        if (userRepo.existsByEmail(newEmail)) throw new UserAlreadyExistsByEmailException(newEmail);
        if (userRepo.existsByUsername(newName)) throw new UserAlreadyExistsByUsernameException(newName);

        String newPassword = client.getPassword().equals(encoder.encode(ticket.getPassword()))
                ? null : ticket.getPassword();

        if (newEmail != null) {
            client.setEmail(newEmail);
            changed = true;
        }
        if (newName != null) {
            client.setUsername(newName);
            changed = true;
        }
        if (newPassword != null) {
            client.setPassword(encoder.encode(newPassword));
            changed = true;
        }

        if (changed) {
            return clientMapper.mapToDto(userRepo.save(client));
        } else throw new UpdateRequestException();
    }

    public ClientDto deleteClient(String username) {
        Client client = findUser(username);
        client.setActive(false);
        return clientMapper.mapToDto(userRepo.save(client));
    }

    private Client findUser(String username) {
        Client client = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        if (!client.isActive()) throw new UserIsDisabledException(username);
        return client;
    }
}

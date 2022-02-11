package org.bankmanagement.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bankmanagement.data_transfer_objects.RegisterTicket;
import org.bankmanagement.data_transfer_objects.UserDto;
import org.bankmanagement.exceptions.UserAlreadyExistsByEmail;
import org.bankmanagement.exceptions.UserAlreadyExistsByUsername;
import org.bankmanagement.mappers.UserMapper;
import org.bankmanagement.models.Role;
import org.bankmanagement.models.User;
import org.bankmanagement.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserDto register(RegisterTicket ticket) {
        String username = ticket.getUsername();
        String email = ticket.getEmail();

        if (userRepo.existsByUsername(username)) {
            throw new UserAlreadyExistsByUsername(username);
        }
        if (userRepo.existsByEmail(email)) {
            throw new UserAlreadyExistsByEmail(email);
        }
        log.info("Register new client");

        User user = new User()
                .setEmail(email)
                .setUsername(username)
                .setPassword(encoder.encode(ticket.getPassword()))
                .setRole(Role.ROLE_CLIENT)
                .setActive(true);
        return userMapper.mapToDto(userRepo.save(user));
    }

}

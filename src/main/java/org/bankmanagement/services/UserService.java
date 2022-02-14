package org.bankmanagement.services;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.data_transfer_objects.RegisterTicket;
import org.bankmanagement.data_transfer_objects.UpdateTicket;
import org.bankmanagement.data_transfer_objects.UserDto;
import org.bankmanagement.exceptions.*;
import org.bankmanagement.mappers.UserMapper;
import org.bankmanagement.models.Role;
import org.bankmanagement.models.User;
import org.bankmanagement.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserDto regUser(RegisterTicket ticket) {
        String email = ticket.getEmail();
        String username = ticket.getUsername();

        if (userRepo.existsByEmail(email)) throw new UserAlreadyExistsByEmailException(email);
        if (userRepo.existsByUsername(username)) throw new UserAlreadyExistsByUsernameException(username);

        User user = new User()
                .setEmail(email)
                .setUsername(username)
                .setPassword(encoder.encode(ticket.getPassword()))
                .setRole(Role.ROLE_CLIENT)
                .setActive(true);

        return userMapper.mapToDto(userRepo.save(user));
    }

    public UserDto getUser(String username) {
        User user = findUser(username);
        return userMapper.mapToDto(user);
    }

    public UserDto updateUser(String username, UpdateTicket ticket) {
        User user = findUser(username);
        boolean changed = false;

        String newEmail = user.getEmail().equals(ticket.getEmail()) ? null : ticket.getEmail();
        String newName = user.getUsername().equals(ticket.getUsername()) ? null : ticket.getUsername();

        if (userRepo.existsByEmail(newEmail)) throw new UserAlreadyExistsByEmailException(newEmail);
        if (userRepo.existsByUsername(newName)) throw new UserAlreadyExistsByUsernameException(newName);

        String newPassword = user.getPassword().equals(encoder.encode(ticket.getPassword()))
                ? null : ticket.getPassword();

        if (newEmail != null) {
            user.setEmail(newEmail);
            changed = true;
        }
        if (newName != null) {
            user.setUsername(newName);
            changed = true;
        }
        if (newPassword != null) {
            user.setPassword(encoder.encode(newPassword));
            changed = true;
        }

        if (changed) {
            return userMapper.mapToDto(userRepo.save(user));
        } else throw new UpdateRequestException();
    }

    public UserDto deleteUser(String username) {
        User user = findUser(username);
        user.setActive(false);
        return userMapper.mapToDto(userRepo.save(user));
    }

    private User findUser(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        if (!user.isActive()) throw new UserIsDisabledException(username);
        return user;
    }
}

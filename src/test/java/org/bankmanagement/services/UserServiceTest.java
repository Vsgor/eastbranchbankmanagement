package org.bankmanagement.services;

import org.bankmanagement.data_transfer_objects.RegisterTicket;
import org.bankmanagement.data_transfer_objects.UpdateTicket;
import org.bankmanagement.data_transfer_objects.UserDto;
import org.bankmanagement.exceptions.*;
import org.bankmanagement.mappers.UserMapper;
import org.bankmanagement.models.Role;
import org.bankmanagement.models.User;
import org.bankmanagement.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserServiceTest {

    private final UserMapper userMapper = Mockito.mock(UserMapper.class);
    private final UserRepository userRepo = Mockito.mock(UserRepository.class);
    private final PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);

    private final UserService service = new UserService(userMapper, userRepo, encoder);

    @Test
    public void getUserShouldThrowUserNotFoundException() {
        when(userRepo.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getUser(any()));

        verify(userRepo).findByUsername(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    public void getUserShouldThrowUserIsDisabledException() {
        String username = "Tutor";
        User user = new User().setUsername(username).setActive(false);

        when(userRepo.findByUsername(username)).thenReturn(Optional.ofNullable(user));

        assertThrows(UserIsDisabledException.class, () -> service.getUser(username));

        verify(userRepo).findByUsername(username);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void getUserShouldSucceed() {
        String username = "Tutor";
        User user = new User().setUsername(username).setActive(true);
        UserDto userDto = new UserDto().setUsername(username).setActive(true);

        when(userRepo.findByUsername(username)).thenReturn(Optional.ofNullable(user));
        when(userMapper.mapToDto(user)).thenReturn(userDto);

        UserDto response = service.getUser(username);

        assertEquals(userDto, response);

        verify(userRepo).findByUsername(username);
        verify(userMapper).mapToDto(user);
    }

    @Test
    public void updateUserShouldThrowUserNotFoundException() {
        when(userRepo.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.updateUser(any(), new UpdateTicket()));

        verify(userRepo, only()).findByUsername(any());
        verifyNoInteractions(userMapper);
        verifyNoInteractions(encoder);
    }

    @Test
    public void updateUserShouldThrowUserIsDisabledException() {
        when(userRepo.findByUsername(any())).thenReturn(Optional.ofNullable(new User().setActive(false)));

        assertThrows(UserIsDisabledException.class, () -> service.updateUser(any(), new UpdateTicket()));

        verify(userRepo, only()).findByUsername(any());
        verifyNoInteractions(userMapper);
        verifyNoInteractions(encoder);
    }

    @Test
    public void updateUserShouldThrowUserAlreadyExistsByEmailException() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";
        User user = new User().setUsername(username).setActive(true).setEmail(email).setPassword(password);

        when(userRepo.findByUsername(username)).thenReturn(Optional.ofNullable(user));
        when(userRepo.existsByEmail(any())).thenReturn(true);

        assertThrows(UserAlreadyExistsByEmailException.class, () -> service.updateUser(username, new UpdateTicket()));

        verify(userRepo).findByUsername(username);
        verify(userRepo).existsByEmail(any());
        verifyNoMoreInteractions(userRepo);
        verifyNoInteractions(userMapper);
        verifyNoInteractions(encoder);
    }

    @Test
    public void updateUserShouldThrowUserAlreadyExistsByUsernameException() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";
        User user = new User().setUsername(username).setActive(true).setEmail(email).setPassword(password);

        when(userRepo.findByUsername(username)).thenReturn(Optional.ofNullable(user));
        when(userRepo.existsByEmail(any())).thenReturn(false);
        when(userRepo.existsByUsername(any())).thenReturn(true);

        assertThrows(UserAlreadyExistsByUsernameException.class, () -> service.updateUser(username, new UpdateTicket()));

        verify(userRepo).findByUsername(username);
        verify(userRepo).existsByEmail(any());
        verify(userRepo).existsByUsername(any());
        verifyNoMoreInteractions(userRepo);
        verifyNoInteractions(userMapper);
        verifyNoInteractions(encoder);
    }

    @Test
    public void updateUserShouldThrowUpdateRequestException() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";
        User user = new User().setUsername(username).setActive(true).setEmail(email).setPassword(password);

        when(userRepo.findByUsername(username)).thenReturn(Optional.ofNullable(user));
        when(userRepo.existsByEmail(any())).thenReturn(false);
        when(userRepo.existsByUsername(any())).thenReturn(false);
        when(encoder.encode(any())).thenReturn(any());

        assertThrows(UpdateRequestException.class, () -> service.updateUser(username, new UpdateTicket()));

        verify(userRepo).findByUsername(username);
        verify(userRepo).existsByUsername(any());
        verify(userRepo).existsByEmail(any());
        verify(encoder, only()).encode(any());
        verifyNoMoreInteractions(userRepo);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void updateUserWithSameDataShouldThrowUpdateRequestException() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";
        User user = new User().setUsername(username).setActive(true).setEmail(email).setPassword(password);
        UpdateTicket ticket = new UpdateTicket().setUsername(username).setPassword(password).setEmail(email);

        when(userRepo.findByUsername(username)).thenReturn(Optional.ofNullable(user));
        when(userRepo.existsByEmail(any())).thenReturn(false);
        when(userRepo.existsByUsername(any())).thenReturn(false);
        when(encoder.encode(password)).thenReturn(password);

        assertThrows(UpdateRequestException.class, () -> service.updateUser(username, ticket));

        verify(userRepo).findByUsername(username);
        verify(userRepo).existsByUsername(any());
        verify(userRepo).existsByEmail(any());
        verify(encoder, only()).encode(password);
        verifyNoMoreInteractions(userRepo);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void updateUserShouldSucceed() {
        String email = "example@tutor.org";
        String username = "Tutor";
        String password = "Rail";

        String newEmail = "immortal@legend.heaven";
        String newName = "Paparapa";
        String newPassword = "Poow";

        User user = new User().setUsername(username).setActive(true).setEmail(email).setPassword(password);
        UpdateTicket ticket = new UpdateTicket().setUsername(newName).setPassword(newPassword).setEmail(newEmail);
        UserDto dto = new UserDto();

        when(userRepo.findByUsername(username)).thenReturn(Optional.ofNullable(user));
        when(userRepo.existsByEmail(newEmail)).thenReturn(false);
        when(userRepo.existsByUsername(newName)).thenReturn(false);
        when(encoder.encode(newPassword)).thenReturn(newPassword);
        when(userRepo.save(user)).thenReturn(user);
        when(userMapper.mapToDto(user)).thenReturn(dto);

//        Response comes from mapper
        UserDto response = service.updateUser(username, ticket);
        assertEquals(dto, response);

//        Check user is updated with all those values
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userArgumentCaptor.capture());
        User value = userArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(newEmail, value.getEmail()),
                () -> assertEquals(newName, value.getUsername()),
                () -> assertEquals(newPassword, value.getPassword())
        );

//        Verify mocks got exact arguments
        verify(userRepo).findByUsername(username);
        verify(userRepo).existsByUsername(newName);
        verify(userRepo).existsByEmail(newEmail);
        verify(userMapper).mapToDto(user);
        verify(encoder, times(2)).encode(newPassword);
    }

    @Test
    public void deleteUserShouldThrowUserNotFoundException() {
        when(userRepo.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.deleteUser(any()));

        verify(userRepo, only()).findByUsername(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    public void deleteUserShouldThrowUserIsDisabledException() {
        String username = "Tutor";
        User user = new User().setUsername(username).setActive(false);

        when(userRepo.findByUsername(username)).thenReturn(Optional.ofNullable(user));

        assertThrows(UserIsDisabledException.class, () -> service.deleteUser(username));

        verify(userRepo, only()).findByUsername(username);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void deleteShouldSucceed() {
        String username = "Tutor";
        User user = new User().setActive(true);
        UserDto userDto = new UserDto();

        when(userRepo.findByUsername(username)).thenReturn(Optional.ofNullable(user));
        when(userRepo.save(user)).thenReturn(user);
        when(userMapper.mapToDto(user)).thenReturn(userDto);

        UserDto response = service.deleteUser(username);

        assertEquals(userDto, response);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepo).save(userArgumentCaptor.capture());

        assertFalse(userArgumentCaptor.getValue().isActive());

        verify(userRepo).findByUsername(username);
        verify(userMapper).mapToDto(user);
    }

    @Test
    public void regUserShouldThrowUserAlreadyExistsByEmailException() {
        when(userRepo.existsByEmail(any())).thenReturn(true);

        assertThrows(UserAlreadyExistsByEmailException.class, () -> service.regUser(new RegisterTicket()));

        verify(userRepo, only()).existsByEmail(any());
        verifyNoInteractions(encoder);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void regUserShouldThrowUserAlreadyExistsByUsernameException() {
        when(userRepo.existsByEmail(any())).thenReturn(false);
        when(userRepo.existsByUsername(any())).thenReturn(true);

        assertThrows(UserAlreadyExistsByUsernameException.class, () -> service.regUser(new RegisterTicket()));

        verify(userRepo).existsByEmail(any());
        verify(userRepo).existsByUsername(any());
        verifyNoMoreInteractions(userRepo);
        verifyNoInteractions(encoder);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void regUserShouldSucceed() {
        String email = "test@tutor.org";
        String username = "Tutor";
        String password = "Rail";

        RegisterTicket ticket = new RegisterTicket().setEmail(email).setUsername(username).setPassword(password);
        User standardUser = new User().setEmail(email).setUsername(username).setPassword(password).setRole(Role.ROLE_CLIENT)
                .setActive(true);
        UserDto dto = new UserDto();

        when(userRepo.existsByEmail(email)).thenReturn(false);
        when(userRepo.existsByUsername(email)).thenReturn(false);
        when(encoder.encode(password)).thenReturn(password);
        when(userRepo.save(standardUser)).thenReturn(standardUser);
        when(userMapper.mapToDto(standardUser)).thenReturn(dto);

        UserDto response = service.regUser(ticket);
        assertEquals(dto, response);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userArgumentCaptor.capture());
        assertEquals(standardUser, userArgumentCaptor.getValue());

        verify(userRepo).existsByEmail(any());
        verify(userRepo).existsByUsername(any());
        verify(encoder).encode(password);
        verify(userMapper).mapToDto(standardUser);
    }


}
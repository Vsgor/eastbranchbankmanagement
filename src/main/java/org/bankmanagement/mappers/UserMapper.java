package org.bankmanagement.mappers;

import org.bankmanagement.data_transfer_objects.UserDetailsImpl;
import org.bankmanagement.data_transfer_objects.UserDto;
import org.bankmanagement.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDetails mapToUserDetails(User user) {
        return new UserDetailsImpl()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .setActive(user.isActive())
                .setRole(user.getRole().toString());
    }

    public UserDto mapToDto(User user) {
        return new UserDto()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setUsername(user.getUsername())
                .setRole(user.getRole().toString())
                .setActive(user.isActive());
    }
}

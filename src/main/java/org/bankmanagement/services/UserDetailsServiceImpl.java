package org.bankmanagement.services;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.exceptions.UserNotFoundException;
import org.bankmanagement.mappers.UserMapper;
import org.bankmanagement.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userMapper.mapToUserDetails(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username)));
    }
}

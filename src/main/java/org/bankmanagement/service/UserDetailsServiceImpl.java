package org.bankmanagement.service;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.entity.Client;
import org.bankmanagement.mapper.ClientMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client = clientService.findClient(username);
        return clientMapper.mapToUserDetails(client);
    }
}

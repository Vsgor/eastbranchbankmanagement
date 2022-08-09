package org.bankmanagement.configuration;

import org.bankmanagement.service.TokenManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;

@TestConfiguration
public class TestWebConfiguration {

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private TokenManager tokenManager;

}

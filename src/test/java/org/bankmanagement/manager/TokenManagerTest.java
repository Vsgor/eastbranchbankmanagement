package org.bankmanagement.manager;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TokenManagerTest {

    private final String issuer = "test issuer";
    @InjectMocks
    private TokenManager tokenManager;

    @BeforeEach
    void setUp() {
        int lifeTimeInMinutes = 1;
        String secret = "test secret";

        ReflectionTestUtils.setField(tokenManager, "issuer", issuer);
        ReflectionTestUtils.setField(tokenManager, "lifeTimeInMinutes", lifeTimeInMinutes);
        ReflectionTestUtils.setField(tokenManager, "secret", secret);
    }

    @Test
    void testCycle() {
        String username = "some username";
        String token = tokenManager.createToken(username);
        DecodedJWT jwt = tokenManager.verifyToken(token);

        assertThat(jwt.getIssuedAt()).isBefore(new Date());
        assertThat(jwt.getExpiresAt()).isAfter(new Date());
        assertThat(jwt.getIssuer()).isEqualTo(issuer);
        assertThat(jwt.getSubject()).isEqualTo(username);
    }

}
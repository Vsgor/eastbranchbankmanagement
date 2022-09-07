package org.bankmanagement.manager;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.bankmanagement.dataobject.JwtPair;
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
        int accessTokenLifeTimeInMinutes = 5;
        int refreshTokenLifeTimeInMinutes = 5;
        String secret = "test secret";

        ReflectionTestUtils.setField(tokenManager, "issuer", issuer);
        ReflectionTestUtils.setField(tokenManager, "accessTokenLifeTimeInMinutes", accessTokenLifeTimeInMinutes);
        ReflectionTestUtils.setField(tokenManager, "refreshTokenLifeTimeInMinutes", refreshTokenLifeTimeInMinutes);
        ReflectionTestUtils.setField(tokenManager, "secret", secret);
    }

    @Test
    void testCycle() {
        String username = "some username";
        JwtPair jwtPair = tokenManager.generateJwtPair(username);
        DecodedJWT decodedAccessToken = tokenManager.verifyToken(jwtPair.getAccessToken());
        DecodedJWT decodedRefreshToken = tokenManager.verifyToken(jwtPair.getRefreshToken());

        assertThat(decodedAccessToken.getIssuedAt()).isBefore(new Date());
        assertThat(decodedAccessToken.getExpiresAt()).isAfter(new Date());
        assertThat(decodedAccessToken.getIssuer()).isEqualTo(issuer);
        assertThat(decodedAccessToken.getSubject()).isEqualTo(username);

        assertThat(decodedRefreshToken.getIssuedAt()).isBefore(new Date());
        assertThat(decodedRefreshToken.getExpiresAt()).isAfter(new Date());
        assertThat(decodedRefreshToken.getIssuer()).isEqualTo(issuer);
        assertThat(decodedRefreshToken.getSubject()).isEqualTo(username);
    }

}
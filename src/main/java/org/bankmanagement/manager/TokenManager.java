package org.bankmanagement.manager;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.bankmanagement.dataobject.JwtPair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class TokenManager {

    @Value("${security.jwt.issuer}")
    private String issuer;
    @Value("${security.jwt.lifetime.access.minutes}")
    private Integer accessTokenLifeTimeInMinutes;
    @Value("${security.jwt.lifetime.refresh.minutes}")
    private Integer refreshTokenLifeTimeInMinutes;
    @Value("${security.jwt.secret}")
    private String secret;

    /**
     * Verify token by algorithm and issuer. Verifier will check expiration date.
     *
     * @param token to verify
     * @return object with decoded token
     * @throws JWTVerificationException if token is expired or invalid
     */
    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT
                .require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }

    /**
     * Generate a pair of access and refresh tokens
     * @param username tokens subject
     * @return Object containing access and refresh tokens
     */
    public JwtPair generateJwtPair(String username) {
        String accessToken = generateToken(username, accessTokenLifeTimeInMinutes);
        String refreshToken = generateToken(username, refreshTokenLifeTimeInMinutes);
        return new JwtPair(accessToken, refreshToken);
    }

    /**
     * Generates token with secret, issuer and lifetime
     * @param username token subject
     * @param lifeTime in minutes
     * @return json web token in string format
     */
    private String generateToken(String username, Integer lifeTime) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.MINUTE, lifeTime);
        Date expiresAt = calendar.getTime();

        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(secret));
    }
}

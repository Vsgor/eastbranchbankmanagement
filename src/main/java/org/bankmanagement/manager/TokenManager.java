package org.bankmanagement.manager;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class TokenManager {

    @Value("${custom.security.issuer}")
    private String issuer;
    @Value("${custom.security.lifetime.minutes}")
    private Integer lifeTimeInMinutes;
    @Value("${custom.security.secret}")
    private String secret;

    /**
     * Verify token by algorithm and issuer. Verifier will check expiration date.
     *
     * @param token to verify
     * @return object with decoded token
     */
    public DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT
                .require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }

    /**
     * Create token with secret, issuer and lifetime
     *
     * @param username subject name
     * @return json web token in string format
     */
    public String createToken(String username) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.MINUTE, lifeTimeInMinutes);
        Date expirationDate = calendar.getTime();

        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }
}

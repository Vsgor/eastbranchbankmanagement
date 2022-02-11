package org.bankmanagement.mappers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class TokenManager {

    @Value("${custom.security.issuer}")
    private String issuer;
    @Value("${custom.security.lifetime}")
    private int lifeTime;
    @Value("${custom.security.secret}")
    private String secret;

    //    Verify token by algorithm and issuer. Verifier will check expiration date.
    public DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT
                .require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }

    //    Create token with secret, issuer and lifetime
    public String createToken(String username) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.MINUTE, lifeTime);
        Date expirationDate = calendar.getTime();

        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }
}

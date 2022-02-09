package org.bankmanagement.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class SecurityService {

    @Value("${security.issuer}")
    private String issuer;
    @Value("${security.lifetime}")
    private int lifeTime;
    @Value("${security.secret}")
    private String secret;


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

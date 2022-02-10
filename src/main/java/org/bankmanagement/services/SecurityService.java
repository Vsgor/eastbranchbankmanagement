package org.bankmanagement.services;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.bankmanagement.data_transfer_objects.RegisterTicket;
import org.bankmanagement.data_transfer_objects.UserDto;
import org.bankmanagement.exceptions.UserAlreadyExistsByEmail;
import org.bankmanagement.exceptions.UserAlreadyExistsByUsername;
import org.bankmanagement.mappers.UserMapper;
import org.bankmanagement.models.Role;
import org.bankmanagement.models.User;
import org.bankmanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserMapper userMapper;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Value("${security.issuer}")
    private String issuer;
    @Value("${security.lifetime}")
    private int lifeTime;
    @Value("${security.secret}")
    private String secret;

    public UserDto register(RegisterTicket ticket) {
        String username = ticket.getUsername();
        String email = ticket.getEmail();

        if (userRepo.existsByUsername(username)) {
            throw new UserAlreadyExistsByUsername(username);
        }
        if (userRepo.existsByEmail(email)) {
            throw new UserAlreadyExistsByEmail(email);
        }

        User user = new User()
                .setEmail(email)
                .setUsername(username)
                .setPassword(encoder.encode(ticket.getPassword()))
                .setRole(Role.ROLE_CLIENT)
                .setActive(true);
        return userMapper.mapToDto(userRepo.save(user));
    }


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

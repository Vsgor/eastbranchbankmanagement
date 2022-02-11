package org.bankmanagement.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.bankmanagement.mappers.TokenManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;
    private final UserDetailsService detailsService;
    private final String endpoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if(!request.getServletPath().startsWith(endpoint)) authorize(request);

        filterChain.doFilter(request, response);
    }
    private void authorize(HttpServletRequest request){
//        Does request have authorization parameter
        String auth = request.getHeader("AUTHORIZATION");
        if (auth != null && auth.startsWith("Bearer ")) {
//        Extract and verify token
            String token = auth.split("\\s")[1].trim();
            DecodedJWT jwt = tokenManager.verifyToken(token);

//        Load user from database and set up authentication token
            UserDetails userDetails = detailsService.loadUserByUsername(jwt.getSubject());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

//        Authenticate
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
}

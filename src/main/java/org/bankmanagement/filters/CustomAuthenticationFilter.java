package org.bankmanagement.filters;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.data_transfer_objects.UserDetailsImpl;
import org.bankmanagement.services.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final SecurityService service;

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        UserDetails userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String token = service.createToken(userDetails.getUsername());
        response.setHeader("access_token", token);
    }
}

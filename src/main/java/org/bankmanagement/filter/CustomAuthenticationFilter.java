package org.bankmanagement.filter;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.dataobject.UserDetailsImpl;
import org.bankmanagement.manager.TokenManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenManager tokenManager;

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) {
        UserDetails userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String token = tokenManager.createToken(userDetails.getUsername());
        response.setHeader("access_token", token);
    }
}

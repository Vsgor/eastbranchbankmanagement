package org.bankmanagement.filter;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.dataobject.JwtPair;
import org.bankmanagement.dataobject.UserDetailsImpl;
import org.bankmanagement.manager.CookieManager;
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
    private final CookieManager cookieManager;

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) {
        UserDetails userDetails = (UserDetailsImpl) authResult.getPrincipal();
        JwtPair jwtPair = tokenManager.generateJwtPair(userDetails.getUsername());
        cookieManager.addAuthCookies(response, jwtPair);
    }
}

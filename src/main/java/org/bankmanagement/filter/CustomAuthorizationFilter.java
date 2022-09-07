package org.bankmanagement.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.bankmanagement.dataobject.JwtPair;
import org.bankmanagement.manager.CookieManager;
import org.bankmanagement.manager.TokenManager;
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
import javax.validation.constraints.NotNull;
import java.io.IOException;

import static java.util.Objects.isNull;

@SuppressWarnings("NullableProblems")
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;
    private final CookieManager cookieManager;
    private final UserDetailsService detailsService;
    private final String loginEndpoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        if (!request.getServletPath().startsWith(loginEndpoint)) authorize(request, response);

        filterChain.doFilter(request, response);
    }

    private void authorize(HttpServletRequest request, HttpServletResponse response) {
        JwtPair jwtPair = cookieManager.getAuthCookies(request);
        if (jwtPair == null) return;

        DecodedJWT decodedJWT = null;
        try {
            decodedJWT = tokenManager.verifyToken(jwtPair.getAccessToken());
        } catch (JWTVerificationException e) {
            try {
                decodedJWT = tokenManager.verifyToken(jwtPair.getRefreshToken());
                cookieManager.addAuthCookies(response, tokenManager.generateJwtPair(decodedJWT.getSubject()));
            } catch (JWTVerificationException exception) {
                cookieManager.removeAuthCookies(response);
            }
        }
        if (isNull(decodedJWT)) return;

        UserDetails userDetails = detailsService.loadUserByUsername(decodedJWT.getSubject());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }


}

package org.bankmanagement.configuration;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.enums.Role;
import org.bankmanagement.filter.CustomAuthenticationFilter;
import org.bankmanagement.filter.CustomAuthorizationFilter;
import org.bankmanagement.manager.CookieManager;
import org.bankmanagement.manager.TokenManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String ADMIN_ENDPOINT = "/admin/**";
    private static final String LOGIN_ENDPOINT = "/login";
    private final TokenManager tokenManager;
    private final CookieManager cookieManager;
    private final UserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConf) throws Exception {
        return authConf.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CustomAuthorizationFilter authorizationFilter =
                new CustomAuthorizationFilter(tokenManager, cookieManager, userDetailsService);
        CustomAuthenticationFilter authenticationFilter =
                new CustomAuthenticationFilter(tokenManager, cookieManager);
        authenticationFilter.setFilterProcessesUrl(LOGIN_ENDPOINT);
        authenticationFilter.setAuthenticationManager(authenticationManager(
                http.getSharedObject(AuthenticationConfiguration.class)
        ));

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(ADMIN_ENDPOINT).hasRole(Role.ROLE_ADMIN.getShortName())
                .antMatchers(LOGIN_ENDPOINT.concat("/**")).anonymous()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(authorizationFilter, CustomAuthenticationFilter.class)
                .addFilter(authenticationFilter)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}

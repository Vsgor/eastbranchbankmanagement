package org.bankmanagement.configuration;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.entity.Role;
import org.bankmanagement.filter.CustomAuthenticationFilter;
import org.bankmanagement.filter.CustomAuthorizationFilter;
import org.bankmanagement.service.TokenManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_ENDPOINT = "/api/login";
    private static final String ADMIN_ENDPOINT = "/api/admin/**";
    private final UserDetailsService userDetailsService;
    private final TokenManager tokenManager;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AbstractAuthenticationProcessingFilter authenticationFilter = new CustomAuthenticationFilter(tokenManager);
        authenticationFilter.setFilterProcessesUrl(LOGIN_ENDPOINT);
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());

        CustomAuthorizationFilter authorizationFilter =
                new CustomAuthorizationFilter(tokenManager, userDetailsService, LOGIN_ENDPOINT);

        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers(LOGIN_ENDPOINT.concat("/**")).permitAll()
                    .antMatchers(ADMIN_ENDPOINT).hasRole(Role.ROLE_ADMIN.getRole())
                    .anyRequest().authenticated()
                .and()
                .addFilterBefore(authorizationFilter, CustomAuthenticationFilter.class)
                .addFilter(authenticationFilter)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}

package com.janisschanbacher.springmicroservicesdemo.authService.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity    // Enable security config. This annotation denotes config for spring security.
public class SecurityCredentialsConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Configure HttpSecurity
     *
     * @param http HttpSecurity object of the authService
     * @throws Exception Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            // make sure we use stateless session; session won't be used to store user's state.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // handle an authorized attempts
            .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .and()
            // Add a filter to validate user credentials and add token in the response header

            // What's the authenticationManager()?
            // An object provided by WebSecurityConfigurerAdapter, used to authenticate the user passing user's credentials
            // The filter needs this auth manager to authenticate the user.
            .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig))
            .authorizeRequests()
            // allow all POST requests
            .antMatchers(HttpMethod.POST, jwtConfig.getUri()).permitAll()
            .antMatchers("/auth/version/**").permitAll()
            // any other requests must be authenticated
            .anyRequest().authenticated();
    }

    // Spring has UserDetailsService interface, which can be overriden to provide our implementation for fetching user from database (or any other source).
    // The UserDetailsService object is used by the auth manager to load the user from database.
    // In addition, we need to define the password encoder also. So, auth manager can compare and verify passwords.

    /**
     * Configure UserDetailsServiceImpl userDetailsService to be used which load users from Database (instead of using
     * default from Spring) and configure it to use the passwordEncoder provided by the passwordEncoder Bean.
     *
     * @param auth AuthenticationManagerBuilder
     * @throws Exception Exeception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * Returns an Instance of JwtConfig
     *
     * @return an Instance of JwtConfig
     */
    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }

    /**
     * Returns a BcryptPasswordEncoder
     *
     * @return Returns a BcryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

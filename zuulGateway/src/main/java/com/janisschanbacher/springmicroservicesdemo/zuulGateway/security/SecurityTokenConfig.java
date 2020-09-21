package com.janisschanbacher.springmicroservicesdemo.zuulGateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity    // Enable security config. This annotation denotes config for spring security.
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
            .exceptionHandling()
            .authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .and()
            // Add a filter to validate the tokens with every request
            .addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
            // authorization requests config
            .authorizeRequests()
            // Allow accessing authentication without yet being authenticated
            .antMatchers(HttpMethod.POST, jwtConfig.getUri()).permitAll()

            // Allow user creation without authentication
            .antMatchers(HttpMethod.POST, "/api/users/**").permitAll()

            // Allow accessing the git version controllers without authentication
            .antMatchers(HttpMethod.GET, "/api/version/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/gateway/version/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/config/version/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/discovery/version/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/users/version/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/auth/version/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/songs/version/**").permitAll()

            // Only allow admin to get users index /
            .antMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/api/users/").hasRole("ADMIN")

            // Any other request must be authenticated
            .anyRequest().authenticated();
    }

    /**
     * Configure UserDetailsServiceImpl userDetailsService to be used which load users from Database (instead of using
     * default from Spring) and configure it to use the passwordEncoder provided by the passwordEncoder Bean.
     *
     * @param auth AuthenticationManagerBuilder
     * @throws Exception Exception
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

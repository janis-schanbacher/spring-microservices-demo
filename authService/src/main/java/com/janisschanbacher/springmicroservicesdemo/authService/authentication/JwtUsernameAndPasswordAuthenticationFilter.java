package com.janisschanbacher.springmicroservicesdemo.authService.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.janisschanbacher.springmicroservicesdemo.authService.model.UserCredentials;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtConfig jwtConfig;
    // to validate the user credentials
    private final AuthenticationManager authManager;
//    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Configures the path to the UsernamePasswordAuthenticationFilter to the uri specified in jwt-Config
     * for the provided AuthenticationManager
     *
     * @param authManager The AuthenticationManager to be configured
     * @param jwtConfig   JwtConfig object that contains the uri to be set
     */
    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager, JwtConfig jwtConfig) {
        this.authManager = authManager;
        this.jwtConfig = jwtConfig;

        // By default, UsernamePasswordAuthenticationFilter listens to "/login" path.
        // Instead use the uri specified in jwtConfig
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtConfig.getUri(), "POST"));
    }

    /**
     * Authenticates the user using the credentials specified in request
     * or throws BadCredentialsExceptions if the the credentials are not valid.
     *
     * @param request  authentication request containing user credentials
     * @param response response
     * @return Authentication object
     * @throws AuthenticationException if the authentication is not successful
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {

        try {
            // 1. Get credentials from request
            UserCredentials creds = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);

            // 2. Create auth object (contains credentials) which will be used by auth manager
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                creds.getUserId(), creds.getPassword(), Collections.emptyList());

            // 3. Authentication manager authenticate the user, and use UserDetialsServiceImpl::loadUserByUsername() method to load the user.
            return authManager.authenticate(authToken);

        } catch (IOException e) {
            throw new BadCredentialsException("Credentials are invalid");
        }
    }

    // Upon successful authentication, generate a token.
    // The 'auth' passed to successfulAuthentication() is the current authenticated user.

    /**
     * Generate a token based on the Authentication object auth (current authenticated user),
     * and add it the the Authentication Header of response
     *
     * @param request  request to be filtered
     * @param response response
     * @param chain    FilterC
     * @param auth     Authorization object containing the currently authenticated user
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
            .setSubject(auth.getName())
            // Convert to list of strings.
            // This is important because it affects the way we get them back in the Gateway.
            .claim("authorities", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000))  // in milliseconds
            .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
            .compact();

        // Add token to header
        response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);
    }
}

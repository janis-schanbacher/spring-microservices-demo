package com.janisschanbacher.springmicroservicesdemo.zuulGateway.security;

import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtConfig jwtConfig;

    /**
     * Constructor, sets jwtConfig
     *
     * @param jwtConfig jwtConfig to be set
     */
    public JwtTokenAuthenticationFilter(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    /**
     * Validates the jwt-token provided in the Authorization header. On success the current user will be
     * authentiated, a currentUserId header that contains the subject of the token will be added to the request and
     * in case the user has the Role 'ADMIN' a isAdmin header with value 'true' will be added to the request and the
     * request will be forwareded to the next filter in the chain/to the destination.
     * If the authentication is not successful the security context will be cleared to ensure that no authentication
     * is done
     *
     * @param request  request to be filtered based on Authroization header
     * @param response response
     * @param chain    filter chain
     * @throws ServletException ServletException
     * @throws IOException      IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        // 1. get the authentication header. Tokens are supposed to be passed in the authentication header
        String header = request.getHeader(jwtConfig.getHeader());

        // 2. validate the header and check the prefix
        if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
            chain.doFilter(request, response); // If not valid, go to the next filter.
            // User is not authenticated, thus he can only access unsecured paths.
            return;
        }

        // 3. Get the token
        String token = header.replace(jwtConfig.getPrefix(), "");

        try {    // exceptions might be thrown in creating the claims if for example the token is expired
//
//			// 4. Validate the token
            Claims claims = Jwts.parser()
                .setSigningKey(jwtConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();
            String username = claims.getSubject();

            if (username != null) {
                @SuppressWarnings("unchecked")
                List<String> authorities = (List<String>) claims.get("authorities");

                // 5. Create auth object
                // UsernamePasswordAuthenticationToken: A built-in object, used by spring to represent the current authenticated / being authenticated user.
                // It needs a list of authorities, which has type of GrantedAuthority interface, where SimpleGrantedAuthority is an implementation of that interface
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

                // 6. Authenticate the user
                // Now, user is authenticated
                SecurityContextHolder.getContext().setAuthentication(auth);
                RequestContext.getCurrentContext().addZuulRequestHeader("currentUserId", username);
                if (authorities.contains("ROLE_ADMIN")) {
                    RequestContext.getCurrentContext().addZuulRequestHeader("isAdmin", "true");
                }
            }
        } catch (Exception e) {
            // In case of failure clear context to so guarantee user won't be authenticated
            SecurityContextHolder.clearContext();
        }

        // go to the next filter in the filter chain
        chain.doFilter(request, response);
    }
}

package com.janisschanbacher.springmicroservicesdemo.zuulGateway.security;

import org.springframework.beans.factory.annotation.Value;

// To use this class outside. You have to
// 1. Define it as a bean, either by adding @Component or use @Bean to instantiate an object from it
// 2. Use the @Autowire to ask spring to auto create it for you, and inject all the values.

// So, If you tried to create an instance manually (i.e. new JwtConfig()). This won't inject all the values.
// Because you didn't ask Spring to do so (it's done by you manually!).
// Also, if, at any time, you tried to instantiate an object that's not defined as a bean
// Don't expect Spring will autowire the fields inside that class object.

public class JwtConfig {

    // Spring doesn't inject/autowire to "static" fields.
    // Link: https://stackoverflow.com/a/6897406
    @Value("${security.jwt.uri:/api/auth/**}")
    private String Uri;

    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.prefix:Bearer }")
    private String prefix;

    @Value("${security.jwt.expiration:#{24*60*60}}")
    private int expiration;

    @Value("${security.jwt.secret:JwtSecretKey123456789012345678901234567890123456789012345678901234567890}")
    private String secret;

    /**
     * Returns uri of the auth service
     *
     * @return uri of the auth service
     */
    public String getUri() {
        return Uri;
    }

    /**
     * Returns the name of the Header that will contain the jwt-token
     *
     * @return the name of the Header that will contain the jwt-token
     */
    public String getHeader() {
        return header;
    }

    /**
     * Returns the prefix of the jwt-token
     *
     * @return the prefix of the jwt-token
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the duration until a new jwt-token will be expired
     *
     * @return the duration until a new jwt-token will be expired
     */
    public int getExpiration() {
        return expiration;
    }

    /**
     * Returns the secret used to create and validate the jwt-tokens
     *
     * @return the secret used to create and validate the jwt-tokens
     */
    public String getSecret() {
        return secret;
    }
}

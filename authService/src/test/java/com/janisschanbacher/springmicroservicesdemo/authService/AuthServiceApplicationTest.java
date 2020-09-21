package com.janisschanbacher.springmicroservicesdemo.authService;

import com.janisschanbacher.springmicroservicesdemo.authService.authentication.JwtConfig;
import com.janisschanbacher.springmicroservicesdemo.authService.controller.GitVersionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AuthServiceApplicationTest {
    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private GitVersionController gitVersionController;

    @Test
    void contextLoads() {
        assertThat(jwtConfig).isNotNull();
        assertThat(passwordEncoder).isNotNull();
        assertThat(gitVersionController).isNotNull();
    }
}

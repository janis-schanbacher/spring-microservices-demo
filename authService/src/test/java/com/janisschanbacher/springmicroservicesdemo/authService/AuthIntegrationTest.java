package com.janisschanbacher.springmicroservicesdemo.authService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.janisschanbacher.springmicroservicesdemo.authService.authentication.JwtConfig;
import com.janisschanbacher.springmicroservicesdemo.authService.model.UserCredentials;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    JwtConfig jwtConfig;

    @Test
    void given_emptyBody_then_status401() throws Exception {
        mockMvc.perform(post("/auth"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_noPassword_then_status401() throws Exception {
        UserCredentials invalidUser = new UserCredentials();
        invalidUser.setUserId("testUser");
        mockMvc.perform(post("/auth")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(invalidUser)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_noUserId_then_status401() throws Exception {
        UserCredentials invalidUser = new UserCredentials();
        invalidUser.setPassword("password");
        mockMvc.perform(post("/auth")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(invalidUser)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_invalidPassword_then_status401() throws Exception {
        UserCredentials invalidUser = new UserCredentials();
        invalidUser.setUserId("testUser");
        invalidUser.setPassword("wrongPassword");
        mockMvc.perform(post("/auth")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(invalidUser)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_invalidUserId_then_status401() throws Exception {
        UserCredentials invalidUser = new UserCredentials();
        invalidUser.setUserId("notExistingId");
        invalidUser.setPassword("password");
        mockMvc.perform(post("/auth")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(invalidUser)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_validCredentials_then_returnBearerTokenAnd200() throws Exception {
        UserCredentials u = new UserCredentials();
        u.setUserId("testUser");
        u.setPassword("password");
        MvcResult result = mockMvc.perform(post("/auth")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(u)))
            .andExpect(status().isOk())
            .andReturn();
        assertTrue(result.getResponse().containsHeader("Authorization"));
        String authHeader = result.getResponse().getHeader("Authorization");
        assertNotNull(authHeader);
        assertTrue(authHeader.startsWith("Bearer "));
        String token = authHeader.replace(jwtConfig.getPrefix(), "");
        Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token).getBody();
        assertEquals("testUser", claims.getSubject());
    }
}

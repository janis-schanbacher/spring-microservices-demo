package com.janisschanbacher.springmicroservicesdemo.zuulGateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Requires configServer, discoveryServer, userManagementService, authService, and songService to be running
@Disabled
@SpringBootTest
@AutoConfigureMockMvc
public class TokenAuthenticationIntegtrationTest {
    @Autowired
    protected MockMvc mockMvc;
    private String validUserToken;
    private String validAdminToken;

    @BeforeEach
    void dataSetup() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content("{ \"userId\": \"mmuster\", \"password\": \"pass1234\" }"))
            .andExpect(status().isOk())
            .andReturn();
        validUserToken = result.getResponse().getHeader("Authorization");

        MvcResult result2 = mockMvc.perform(post("/api/auth")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content("{ \"userId\": \"admin\", \"password\": \"pass1234\" }"))
            .andExpect(status().isOk())
            .andReturn();
        validAdminToken = result2.getResponse().getHeader("Authorization");
    }

    @Test
    void given_authenticate_when_noAuthorizationHeader_then_status200() throws Exception {
        mockMvc.perform(post("/api/auth")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content("{ \"userId\": \"mmuster\", \"password\": \"pass1234\" }"))
            .andExpect(status().isOk());
    }

    @Test
    void given_accessGitVersionController_when_noAuthorizationHeader_then_status200() throws Exception {
        mockMvc.perform(get("/api/version"))
            .andExpect(status().isOk());
    }

    @Test
    void given_accessGatewayGitVersionController_when_noAuthorizationHeader_then_status200() throws Exception {
        mockMvc.perform(get("/api/gateway/version"))
            .andExpect(status().isOk());
    }

    @Test
    void given_accessGitConfigVersionController_when_noAuthorizationHeader_then_status200() throws Exception {
        mockMvc.perform(get("/api/config/version"))
            .andExpect(status().isOk());
    }

    @Test
    void given_accessDiscoveryGitVersionController_when_noAuthorizationHeader_then_status200() throws Exception {
        mockMvc.perform(get("/api/discovery/version"))
            .andExpect(status().isOk());
    }

    @Test
    void given_accessUsersGitVersionController_when_noAuthorizationHeader_then_status200() throws Exception {
        mockMvc.perform(get("/api/users/version"))
            .andExpect(status().isOk());
    }

    @Test
    void given_accessAuthGitVersionController_when_noAuthorizationHeader_then_status200() throws Exception {
        mockMvc.perform(get("/api/auth/version"))
            .andExpect(status().isOk());
    }

    @Test
    void given_accessSongsGitVersionController_when_noAuthorizationHeader_then_status200() throws Exception {
        mockMvc.perform(get("/api/songs/version"))
            .andExpect(status().isOk());
    }

    @Test
    void given_accessCreateUser_when_noAuthorizationHeader_then_status200() throws Exception {
        // in case the testUser2 already exists.
        mockMvc.perform(delete("/api/users/testUser2")
            .header("Authorization", validAdminToken));

        mockMvc.perform(post("/api/users")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content("{ \"username\": \"testUser2\", \"password\": \"password\" }"))
            .andExpect(status().isCreated());

        // cleanup
        mockMvc.perform(delete("/api/users/testUser2")
            .header("Authorization", validAdminToken))
            .andExpect(status().isNoContent());
    }

    @Test
    void given_accessUserIndex_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessSongIndex_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(get("/api/songs"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessPlaylistIndex_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(get("/api/songlists"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessGetUser_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(get("/api/users/mmuster"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessGetSong_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(get("/api/songs/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessGetPlaylist_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(get("/api/songlists/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessPostSong_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(post("/api/songs"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessPostPlaylist_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(post("/api/songlists"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessPutUser_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(put("/api/users/mmuster"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessPutSong_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(put("/api/songs/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessPutPlaylist_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(put("/api/songlists/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessDeleteUser_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(delete("/api/users/mmuster"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessDeleteSong_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(delete("/api/songs/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessDeletePlaylist_when_noAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(delete("/api/songlists/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void given_accessUserIndex_when_userAuthorizationHeader_then_status403() throws Exception {
        mockMvc.perform(get("/api/users")
            .header("Authorization", validUserToken))
            .andExpect(status().isForbidden());
    }

    @Test
    void given_accessUserIndex_when_adminAuthorizationHeader_then_status200() throws Exception {
        mockMvc.perform(get("/api/users")
            .header("Authorization", validAdminToken))
            .andExpect(status().isOk());
    }

    @Test
    void given_postPlaylist_when_validAuthorizationHeader_then_status201() throws Exception {
        mockMvc.perform(post("/api/songlists")
            .header("Authorization", validUserToken)
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content("{ \"isPrivate\": true, \"name\": \"MPrivateList\", \"songList\": [] }"))
            .andExpect(status().isCreated());
    }

    @Test
    void given_accessPlaylistService_when_validAuthorizationHeader_then_addUsernameToRequestAndThusGetPlaylistAnd200() throws Exception {
        // setup, equal to test given_postPlaylist_when_validAuthorizationHeader_then_status201
        MvcResult result = mockMvc.perform(post("/api/songlists")
            .header("Authorization", validUserToken)
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content("{ \"isPrivate\": true, \"name\": \"MPrivateList\", \"songList\": [] }"))
            .andReturn();
        String locationSavedPlaylist = result.getResponse().getHeader("Location");
        assertNotNull(locationSavedPlaylist);

        // actual test
        mockMvc.perform(get(locationSavedPlaylist)
            .header("Authorization", validUserToken))
            .andExpect(status().isOk())
            .andExpect(content().json("{ \"isPrivate\": true, \"name\": \"MPrivateList\", \"songList\": [] }"))
            .andReturn();
    }

    @Test
    void given_accessSecuredService_when_tokenIsExpired_then_status401() throws Exception {
        mockMvc.perform(get("/api/songs")
            // expired token
            .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjAwMjU2MTYzLCJleHAiOjE2MDAyNjI1NjN9.0n-ua_0TaBVBAJv346JhJm2CU-H0dgY-fhXC6FDnhQdFo1xanvEIvVXVWwL2gC9gherDyCglAIwXY9st4jVy-g"))
            .andExpect(status().isUnauthorized());
    }
}

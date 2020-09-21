package com.janisschanbacher.springmicroservicesdemo.usermanagementservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.janisschanbacher.springmicroservicesdemo.usermanagementservice.document.User;
import com.janisschanbacher.springmicroservicesdemo.usermanagementservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UsersIntegrationTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void dataSetup() {
        userRepository.deleteAll();
        userRepository.save(new User("testUser", "testFirst", "testLast", "password", "USER"));
        userRepository.save(new User("testAdmin", "adminFirst", "adminLast", "password", "ADMIN"));
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
    }

    // index

    @Test
    void given_getIndex_then_returnUsersAnd200() throws Exception {
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].username").value("testAdmin"))
            .andExpect(jsonPath("$[0].firstName").value("adminFirst"))
            .andExpect(jsonPath("$[0].lastName").value("adminLast"))
            .andExpect(jsonPath("$[0].password").exists())
            .andExpect(jsonPath("$[0].role").value("ADMIN"))
            .andExpect(jsonPath("$[1].username").value("testUser"))
            .andExpect(jsonPath("$[1].firstName").value("testFirst"))
            .andExpect(jsonPath("$[1].lastName").value("testLast"))
            .andExpect(jsonPath("$[1].password").exists())
            .andExpect(jsonPath("$[1].role").value("USER"));
    }

    // get

    @Test
    void given_getWithExistingUsername_then_returnUserAnd200() throws Exception {
        mockMvc.perform(get("/users/testUser"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("testUser"))
            .andExpect(jsonPath("$.firstName").value("testFirst"))
            .andExpect(jsonPath("$.lastName").value("testLast"))
            .andExpect(jsonPath("$.password").exists());
    }

    @Test
    void given_getWithNotExistingUsername_then_status404() throws Exception {
        mockMvc.perform(get("/users/noUser"))
            .andExpect(status().isNotFound());
    }

    // post

    @Test
    void given_postWithEmptyBody_then_badRequest() throws Exception {
        mockMvc.perform(post("/users")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_postWithoutUsername_then_badRequest() throws Exception {
        User newUser = new User(null, "testFirst", "testLast", "password", "USER");
        mockMvc.perform(post("/users")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(newUser)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("username field is required and may not be empty"));
    }

    @Test
    void given_postWithEmptyUsername_then_badRequest() throws Exception {
        User newUser = new User("", "testFirst", "testLast", "password", "USER");
        mockMvc.perform(post("/users")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(newUser)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("username field is required and may not be empty"));
    }

    @Test
    void given_postWithoutPassword_then_badRequest() throws Exception {
        User newUser = new User("newUser", "testFirst", "testLast", null, "USER");
        mockMvc.perform(post("/users")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(newUser)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("password field is required"));
    }

    @Test
    void given_postWithUnavailableUsername_then_HintAnd400() throws Exception {
        User newUser = new User("testUser", "newFirst", "newLast", "newPassword", "USER");
        mockMvc.perform(post("/users")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(newUser)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("username is not available"));
    }

    @Test
    void given_postWithInvalidJson_then_returnBadRequest() throws Exception {
        mockMvc.perform(post("/users")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString("{username: invalid format missing quotes}")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_post_then_saveUserAndReturnLocationAnd201() throws Exception {
        User newUser = new User("newUser", "testFirst", "testLast", "password", "USER");
        mockMvc.perform(post("/users")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(newUser)))
            .andExpect(header().string("Location", "/api/users/" + newUser.getUsername()))
            .andExpect(status().isCreated());
        assertTrue(userRepository.findByUsername("newUser").isPresent());
    }

    @Test
    void given_postOnlyUsernameAndPassword_then_saveUserAndReturnLocationAnd201() throws Exception {
        User newUser = new User("newUser", null, null, "password", null);
        mockMvc.perform(post("/users")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(newUser)))
            .andExpect(header().string("Location", "/api/users/" + newUser.getUsername()))
            .andExpect(status().isCreated());
        assertTrue(userRepository.findById("newUser").isPresent());
    }

    @Test
    void given_postAdmin_when_currentUserIsNotAdmin_then_status403() throws Exception {
        User newUser = new User("newAdmin", "testFirst", "testLast", "password", "ADMIN");
        mockMvc.perform(post("/users")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(newUser)))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/users")
            .header("cuurentUserId", "testUser")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(newUser)))
            .andExpect(status().isForbidden());
    }

    @Test
    void given_postAdmin_when_currentUserIsAdmin_then_saveUserAsAdminAndReturnLocationAnd201() throws Exception {
        User newAdmin = new User("newAdmin", "testFirst", "testLast", "password", "ADMIN");
        mockMvc.perform(post("/users")
            .header("cuurentUserId", "testAdmin")
            .header("isAdmin", "true")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(newAdmin)))
            .andExpect(header().string("Location", "/api/users/" + newAdmin.getUsername()))
            .andExpect(status().isCreated());

        Optional<User> savedUser = userRepository.findById("newAdmin");
        assertTrue(savedUser.isPresent());
        assertEquals(savedUser.get().getRole(), newAdmin.getRole());
    }

    // put

    @Test
    void given_put_when_currentUserIdHeaderIsMissing__then_badRequest() throws Exception {
        User testUserV2 = new User("testUser", "new testFirst", "new testLast", "new password", "USER");
        mockMvc.perform(put("/users/testUser")
            .content(mapper.writeValueAsString(testUserV2))
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason("Missing request header 'currentUserId' for method parameter of type String"));
    }

    @Test
    void given_putWithEmptyBody_then_badRequest() throws Exception {
        mockMvc.perform(put("/users/testUser")
            .header("currentUserId", "testUser")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_putWithNotExistingUsername_then_notFound() throws Exception {
        User user = new User("notExistingUsername", null, null, "password", null);
        mockMvc.perform(put("/users/notExistingUsername")
            .header("currentUserId", "notExistingUsername")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(user)))
            .andExpect(status().isNotFound());
    }

    @Test
    void given_putUsernameOfUrlNotEqualToUsernameField_then_badRequest() throws Exception {
        User testUserV2 = new User("different", "testFirst", "testLast", "password", "USER");
        mockMvc.perform(put("/users/testUser")
            .header("currentUserId", "testUser")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(testUserV2)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("username field has to equal username specified in url"));
    }

    @Test
    void given_putWithoutUsername_then_badRequest() throws Exception {
        User testUserV2 = new User(null, "testFirst", "testLast", "password", "USER");
        mockMvc.perform(put("/users/testUser")
            .header("currentUserId", "testUser")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(testUserV2)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("username field is required and may not be empty"));
    }

    @Test
    void given_putWithEmptyUsername_then_badRequest() throws Exception {
        User testUserV2 = new User("", "testFirst", "testLast", "password", "USER");
        mockMvc.perform(put("/users/testUser")
            .header("currentUserId", "testUser")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(testUserV2)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("username field is required and may not be empty"));
    }

    @Test
    void given_putWithoutPassword_then_badRequest() throws Exception {
        User testUserV2 = new User("testUser", "testFirst", "testLast", null, "USER");
        mockMvc.perform(put("/users/testUser")
            .header("currentUserId", "testUser")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(testUserV2)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("password field is required"));
    }

    @Test
    void given_putWithInvalidJson_then_returnBadRequest() throws Exception {
        mockMvc.perform(put("/users/testUser")
            .header("currentUserId", "testUser")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString("{username: invalid format missing quotes}")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_put_when_currentUserIsNotUserToBeUpdatedNorAdmin_then_status403() throws Exception {
        User testUserV2 = new User("testUser", "new testFirst", "new testLast", "new password", "USER");
        mockMvc.perform(put("/users/testUser")
            .header("currentUserId", "otherUser")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(testUserV2)))
            .andExpect(status().isForbidden());
    }

    @Test
    void given_put_when_currentUserIsUserToBeUpdated_then_updateUserAnd204() throws Exception {
        User testUserV2 = new User("testUser", "new testFirst", "new testLast", "new password", "USER");
        mockMvc.perform(put("/users/testUser")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .header("currentUserId", "testUser")
            .content(mapper.writeValueAsString(testUserV2)))
            .andExpect(status().isNoContent());
        Optional<User> savedUser = userRepository.findById("testUser");
        assertTrue(savedUser.isPresent());
        assertEquals(savedUser.get().getUsername(), testUserV2.getUsername());
        assertEquals(savedUser.get().getFirstName(), testUserV2.getFirstName());
        assertEquals(savedUser.get().getLastName(), testUserV2.getLastName());
        assertEquals(savedUser.get().getRole(), testUserV2.getRole());
    }

    @Test
    void given_put_when_currentUserIsAdmin_then_updateUserAnd204() throws Exception {
        User testUserV2 = new User("testUser", "new testFirst", "new testLast", "new password", "ADMIN");
        mockMvc.perform(put("/users/testUser")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .header("currentUserId", "someAdmin")
            .header("isAdmin", "true")
            .content(mapper.writeValueAsString(testUserV2)))
            .andExpect(status().isNoContent());
        Optional<User> savedUser = userRepository.findById("testUser");
        assertTrue(savedUser.isPresent());
        assertEquals(savedUser.get().getUsername(), testUserV2.getUsername());
        assertEquals(savedUser.get().getFirstName(), testUserV2.getFirstName());
        assertEquals(savedUser.get().getLastName(), testUserV2.getLastName());
        assertEquals(savedUser.get().getRole(), testUserV2.getRole());
    }

    @Test
    void given_putOnlyUsernameAndPassword_when_currentUserIsUserToBeUpdated_then_updateUserAnd204() throws Exception {
        User testUserV2 = new User("testUser", null, null, "new password", null);
        mockMvc.perform(put("/users/testUser")
            .header("currentUserId", "testUser")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(testUserV2)))
            .andExpect(status().isNoContent());
        Optional<User> savedUser = userRepository.findById("testUser");
        assertTrue(savedUser.isPresent());
        assertEquals(savedUser.get().getUsername(), testUserV2.getUsername());
        assertEquals(savedUser.get().getFirstName(), testUserV2.getFirstName());
        assertEquals(savedUser.get().getLastName(), testUserV2.getLastName());
        assertEquals(savedUser.get().getRole(), testUserV2.getRole());
    }

    @Test
    void given_putMakeAdmin_when_currentUserIsNotAdmin_then_status403() throws Exception {
        User testUserV2 = new User("testUser", "testFirst", "testLast", "password", "ADMIN");
        mockMvc.perform(put("/users/testUser")
            .header("currentUserId", "testUser")
            .header("isAdmin", "false")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(testUserV2)))
            .andExpect(status().isForbidden());
    }

    @Test
    void given_putMakeAdmin_when_currentUserIsAdmin_then_updateRoleAnd204() throws Exception {
        User testUserV2 = new User("testUser", "testFirst", "testLast", "password", "ADMIN");
        mockMvc.perform(put("/users/testUser")
            .header("currentUserId", "testAdmin")
            .header("isAdmin", "true")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(testUserV2)))
            .andExpect(status().isNoContent());

        Optional<User> savedUser = userRepository.findById("testUser");
        assertTrue(savedUser.isPresent());
        assertEquals(savedUser.get().getRole(), testUserV2.getRole());
    }

    // delete

    @Test
    void given_deleteWithNotExistingUsername_then_notFound() throws Exception {
        mockMvc.perform(delete("/users/notExistingUser")
            .header("currentUserId", "notExistingUser"))
            .andExpect(status().isNotFound());
    }

    @Test
    void given_delete_when_currentUserIsNotUserToBeDeletedNorAdmin_then_status403() throws Exception {
        mockMvc.perform(delete("/users/testUser")
            .header("currentUserId", "otherUser")
            .header("isAdmin", "false"))
            .andExpect(status().isForbidden());
        assertTrue(userRepository.findById("testUser").isPresent());
    }

    @Test
    void given_delete_when_currentUserIsUserToBeDeleted_then_deleteUserAnd204() throws Exception {
        mockMvc.perform(delete("/users/testUser")
            .header("currentUserId", "testUser"))
            .andExpect(status().isNoContent());
        assertTrue(userRepository.findById("testUser").isEmpty());
    }

    @Test
    void given_delete_when_currentUserIsAdmin_then_deleteUserAnd204() throws Exception {
        mockMvc.perform(delete("/users/testUser")
            .header("currentUserId", "testUser")
            .header("isAdmin", "true"))
            .andExpect(status().isNoContent());
        assertTrue(userRepository.findById("testUser").isEmpty());
    }
}

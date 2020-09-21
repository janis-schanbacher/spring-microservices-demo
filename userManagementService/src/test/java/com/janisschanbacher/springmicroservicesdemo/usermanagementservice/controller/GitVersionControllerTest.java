package com.janisschanbacher.springmicroservicesdemo.usermanagementservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GitVersionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void given_accessVersionController_then_returnInfoAnd200() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/version"))
            .andExpect(status().isOk())
            .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("git.dirty"));
    }
}

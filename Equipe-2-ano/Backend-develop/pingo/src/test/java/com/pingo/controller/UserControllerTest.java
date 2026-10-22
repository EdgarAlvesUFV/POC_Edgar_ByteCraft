package com.pingo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authenticate_WhenNotExistingUser_CreateNewUser() throws Exception {
        mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.nickname").value("admin"));
    }

    @Test
    void authenticate_WhenExistingUser_ShouldAuthenticate() throws Exception {
        mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.nickname").value("admin"));
    }

    @Test
    void authenticate_WhenAlreadyExistingUser_ShouldForbbiden() throws Exception {
        mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"123\"}")) 
            .andExpect(status().isForbidden());
    }
}

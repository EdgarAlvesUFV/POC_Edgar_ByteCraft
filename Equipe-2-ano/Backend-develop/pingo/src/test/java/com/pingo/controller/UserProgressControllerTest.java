package com.pingo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserProgressControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void saveProgress_WhenNotExistingUser_ShouldForbidden() throws Exception {
        MvcResult user = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
                .andReturn();
        String response = user.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(response).get("accessToken").asText();
        mockMvc.perform(post("/progress/save")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"user\", \"currentStage\": 1}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void saveProgress_WhenNotExistingStage_ShouldForbidden() throws Exception {
        MvcResult user = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
                .andReturn();
        String response = user.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(response).get("accessToken").asText();
        mockMvc.perform(post("/progress/save")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"user\", \"currentStage\": 23}"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Sql(scripts = {
        "/cleanup.sql",
        "/users_data.sql",
        "/stages_data.sql",
        "/user_progress_data.sql"
    })
    void saveProgress_WhenExistingUserAndNotProgress_ShouldCreateSaveProgress() throws Exception {
        MvcResult user = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
                .andReturn();
        String response = user.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(response).get("accessToken").asText();
        mockMvc.perform(post("/progress/save")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"test\", \"currentStage\": 11 }")) 
            .andExpect(status().isOk())
            .andExpect(content().string("Progresso do usuário criado com sucesso"));
    }

    @Test
    @Sql(scripts = {
        "/cleanup.sql",
        "/users_data.sql",
        "/stages_data.sql",
        "/user_progress_data.sql"
    })
    void saveProgress_WhenExistingUserAndProgress_ShouldPatchProgress() throws Exception {
        MvcResult user = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
                .andReturn();
        String response = user.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(response).get("accessToken").asText();

        mockMvc.perform(post("/progress/save")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"user\", \"currentStage\": 11 }")) 
            .andExpect(status().isOk())
            .andExpect(content().string("Progresso do usuário atualizado com sucesso"));
    }
   

    @Test
    void getUserProgress_WhenNotExistingUser_ShouldForbidden() throws Exception {
        MvcResult user = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
                .andReturn();
        String response = user.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(response).get("accessToken").asText();
        mockMvc.perform(get("/progress/get?nickname=usertest")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }

    @Test
    @Sql(scripts = {
        "/cleanup.sql",
        "/users_data.sql",
        "/stages_data.sql",
        "/user_progress_data.sql"
    })
    void getUserProgress_WhenExistingUser_ShouldReturnProgress() throws Exception {
        MvcResult user = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
                .andReturn();
        String response = user.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(response).get("accessToken").asText();

        mockMvc.perform(get("/progress/get?nickname=user")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"nickname\":\"user\",\"currentStage\": 10}")); 

    }
}

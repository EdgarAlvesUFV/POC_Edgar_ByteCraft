package com.pingo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("/cleanup.sql")
    @Sql("/stages_data.sql")
    public void createStage_WhenStageNotExist_ShouldCreateStage() throws Exception {
        MvcResult user = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
                .andReturn();
        String response = user.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(response).get("accessToken").asText();
        mockMvc.perform(post("/stage/create")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Fase 1\", \"number\": 1 }"))
                .andExpect(status().isOk());
    }

    @Test
    @Sql("/cleanup.sql")
    @Sql("/stages_data.sql")
    public void createStage_WhenNameAlreadyExist_ShouldForbbiden() throws Exception {
        MvcResult user = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
                .andReturn();
        String response = user.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(response).get("accessToken").asText();
        mockMvc.perform(post("/stage/create")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"TESTE\", \"number\": 1 }"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql("/cleanup.sql")
    @Sql("/stages_data.sql")
    public void createStage_WhenNumberAlreadyExist_ShouldForbbiden() throws Exception {
        MvcResult user = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
                .andReturn();
        String response = user.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(response).get("accessToken").asText();
        mockMvc.perform(post("/stage/create")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Fase 2\", \"number\": 10 }"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql("/cleanup.sql")
    @Sql("/stages_data.sql")
    public void listStages_WhenExistStages_ShouldReturnStages() throws Exception {
        MvcResult user = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}")) 
                .andReturn();
        String response = user.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(response).get("accessToken").asText();
        mockMvc.perform(get("/stage/list")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Sql("/cleanup.sql")
    public void listStages_WhenNotExistStages_ShouldReturnEmptyList() throws Exception {
        MvcResult user = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"admin\", \"password\":\"1234\"}"))
                .andReturn();
        String response = user.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(response).get("accessToken").asText();

        mockMvc.perform(get("/stage/list")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}   
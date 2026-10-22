package com.pingo.controller;

import com.pingo.model.dto.NetworkDeviceResponseDTO;
import com.pingo.model.dto.PhaseSetupResponseDTO;
import com.pingo.model.entity.DeviceType;
import com.pingo.service.interfaces.NetworkDeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(NetworkDeviceController.class)
public class NetworkDeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NetworkDeviceService networkDeviceService;

    @Test
    public void getPhaseSetupShouldReturnPhaseSetup() throws Exception {
      
        List<NetworkDeviceResponseDTO> devices = Arrays.asList(
            new NetworkDeviceResponseDTO(1, "modem", DeviceType.fromDbValue(0), false, 0, 0),  
            new NetworkDeviceResponseDTO(2, "switch", DeviceType.fromDbValue(4), false, 0, 1),  
            new NetworkDeviceResponseDTO(3, "modem", DeviceType.fromDbValue(2), false, 0, 2),   
            new NetworkDeviceResponseDTO(4, "switch", DeviceType.fromDbValue(4), false, 1, 0),
            new NetworkDeviceResponseDTO(5, "switch", DeviceType.fromDbValue(4), true, 1, 1),   
            new NetworkDeviceResponseDTO(6, "switch", DeviceType.fromDbValue(4), false, 1, 2),
            new NetworkDeviceResponseDTO(7, "modem", DeviceType.fromDbValue(0), false, 2, 0),
            new NetworkDeviceResponseDTO(8, "switch", DeviceType.fromDbValue(4), false, 2, 1),
            new NetworkDeviceResponseDTO(9, "modem", DeviceType.fromDbValue(2), false, 2, 2)
        );

            List<List<int[]>> correctPath = Arrays.asList(
                Arrays.asList(new int[]{0,0}),
                Arrays.asList(new int[]{0,1}),
                Arrays.asList(new int[]{0,2})
            );
        PhaseSetupResponseDTO phaseSetup = new PhaseSetupResponseDTO(devices, correctPath);

        when(networkDeviceService.getPhaseSetup()).thenReturn(phaseSetup);

        mockMvc.perform(get("/api/network-devices/phase-setup"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.devices").exists())
            .andExpect(jsonPath("$.correctPath").exists())
            .andExpect(jsonPath("$.devices.length()").value(9))  
           
            .andExpect(jsonPath("$.devices[0].id").value(1))
            .andExpect(jsonPath("$.devices[0].name").value("modem"))
            .andExpect(jsonPath("$.devices[0].type").value(1))
            .andExpect(jsonPath("$.devices[0].hasDefect").value(false))
            .andExpect(jsonPath("$.devices[0].positionX").value(0))
            .andExpect(jsonPath("$.devices[0].positionY").value(0))
       
            .andExpect(jsonPath("$.correctPath").isArray())
            .andExpect(jsonPath("$.correctPath.length()").value(3))
            .andExpect(jsonPath("$.correctPath[0]").value(1))  
            .andExpect(jsonPath("$.correctPath[1]").value(2))  
            .andExpect(jsonPath("$.correctPath[2]").value(3)); 
    }

    @Test
    public void getPhaseSetupShouldHaveValidDevicePositions() throws Exception {
        PhaseSetupResponseDTO phaseSetup = networkDeviceService.getPhaseSetup();

        mockMvc.perform(get("/api/network-devices/phase-setup"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.devices[*].positionX").value(org.hamcrest.Matchers.everyItem(
                org.hamcrest.Matchers.allOf(
                    org.hamcrest.Matchers.greaterThanOrEqualTo(0),
                    org.hamcrest.Matchers.lessThan(3)
                )
            )))
            .andExpect(jsonPath("$.devices[*].positionY").value(org.hamcrest.Matchers.everyItem(
                org.hamcrest.Matchers.allOf(
                    org.hamcrest.Matchers.greaterThanOrEqualTo(0),
                    org.hamcrest.Matchers.lessThan(3)
                )
            )));
    }
}
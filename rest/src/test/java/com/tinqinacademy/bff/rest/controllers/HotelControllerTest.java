package com.tinqinacademy.bff.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.authentication.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.hotel.api.RestAPIRoutes;
import com.tinqinacademy.hotel.api.enumerations.BathroomType;
import com.tinqinacademy.hotel.api.enumerations.BedSize;
import com.tinqinacademy.hotel.api.operations.getroom.GetRoomOutput;
import com.tinqinacademy.hotel.restexport.HotelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HotelClient hotelClient;

    @Test
    void shouldRespondWithRoomDataAndOkStatus() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        GetRoomOutput expectedOutput = GetRoomOutput
                .builder()
                .bedCount(1)
                .price(BigDecimal.valueOf(20000))
                .floor(4)
                .bedSizes(List.of(BedSize.SINGLE))
                .bathroomType(BathroomType.PRIVATE)
                .datesOccupied(List.of())
                .id(UUID.fromString(roomId))
                .build();

        when(hotelClient.getRoomById(roomId)).thenReturn(expectedOutput);

        mockMvc.perform(get(RestAPIRoutes.GET_ROOM_DETAILS, roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bedCount").value(expectedOutput.getBedCount()))
                .andExpect(jsonPath("$.price").value(expectedOutput.getPrice()))
                .andExpect(jsonPath("$.id").value(expectedOutput.getId().toString()));
    }

    @Test
    void shouldRespondWithBadRequestWhenProvidedInvalidRoomIdWhenGettingRoomDetails() throws Exception {
        String roomId = "invalid";

        mockMvc.perform(get(RestAPIRoutes.GET_ROOM_DETAILS, roomId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithNotFoundWhenProvidedUnknownRoomIdWhenGettingRoomDetails() throws Exception {
        String roomId = UUID.randomUUID().toString();

        when(hotelClient.getRoomById(roomId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(RestAPIRoutes.GET_ROOM_DETAILS, roomId))
                .andExpect(status().isNotFound());
    }

}
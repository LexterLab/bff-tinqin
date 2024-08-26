package com.tinqinacademy.bff.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.authentication.api.operations.generateaccesstoken.GetUsernameFromTokenOutput;
import com.tinqinacademy.authentication.api.operations.loaduserdetails.LoadUserDetailsInput;
import com.tinqinacademy.authentication.api.operations.loaduserdetails.LoadUserDetailsOutput;
import com.tinqinacademy.authentication.api.operations.validateacesstoken.ValidateAccessTokenInput;
import com.tinqinacademy.authentication.api.operations.validateacesstoken.ValidateAccessTokenOutput;
import com.tinqinacademy.authentication.restexport.AuthenticationClient;
import com.tinqinacademy.bff.api.enumerations.BathroomType;
import com.tinqinacademy.bff.api.enumerations.BedSize;
import com.tinqinacademy.bff.api.operations.createroom.CreateRoomRequest;
import com.tinqinacademy.hotel.api.RestAPIRoutes;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.restexport.HotelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HotelClient hotelClient;

    @MockBean
    private AuthenticationClient authenticationClient;


    @Test
    void shouldRespondWithCreatedAndRoomIdWhenCreatingRoom() throws Exception {
        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();


        CreateRoomRequest request = CreateRoomRequest.builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(4)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        CreateRoomOutput expectedOutput = CreateRoomOutput
                .builder()
                .roomId(UUID.randomUUID().toString())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.createRoom(any(CreateRoomInput.class))).thenReturn(expectedOutput);


        mockMvc.perform(post(RestAPIRoutes.CREATE_ROOM)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").value(expectedOutput.getRoomId()));
    }

    @Test
    void shouldRespondWithForbiddenWhenCreatingRoomWithInsufficientRights() throws Exception {
        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();


        CreateRoomRequest request = CreateRoomRequest.builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(4)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


        mockMvc.perform(post(RestAPIRoutes.CREATE_ROOM)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRespondWithUnauthorizedWhenCreatingRoomWithoutAuthentication() throws Exception {
        CreateRoomRequest request = CreateRoomRequest.builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(4)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        mockMvc.perform(post(RestAPIRoutes.CREATE_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRespondWithBadRequestWhenCreatingRoomWithNullBathroomType() throws Exception {
        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        CreateRoomRequest request = CreateRoomRequest.builder()
                .bathroomType(null)
                .floor(4)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


        mockMvc.perform(post(RestAPIRoutes.CREATE_ROOM)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithBadRequestWhenCreatingRoomWithBelowMinFloor() throws Exception {
        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        CreateRoomRequest request = CreateRoomRequest.builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(0)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


        mockMvc.perform(post(RestAPIRoutes.CREATE_ROOM)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithBadRequestWhenCreatingRoomWithAboveMaxFloor() throws Exception {
        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        CreateRoomRequest request = CreateRoomRequest.builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(11)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


        mockMvc.perform(post(RestAPIRoutes.CREATE_ROOM)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithBadRequestWhenCreatingRoomWithNullRoomNo() throws Exception {
        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        CreateRoomRequest request = CreateRoomRequest.builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(3)
                .roomNo(null)
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


        mockMvc.perform(post(RestAPIRoutes.CREATE_ROOM)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithBadRequestWhenCreatingRoomWithNullFloor() throws Exception {
        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        CreateRoomRequest request = CreateRoomRequest.builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(null)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


        mockMvc.perform(post(RestAPIRoutes.CREATE_ROOM)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithBadRequestWhenCreatingRoomWithInvalidRoomNo() throws Exception {
        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        CreateRoomRequest request = CreateRoomRequest.builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(3)
                .roomNo("201")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


        mockMvc.perform(post(RestAPIRoutes.CREATE_ROOM)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithBadRequestWhenCreatingRoomWithInvalidPrice() throws Exception {
        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        CreateRoomRequest request = CreateRoomRequest.builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(3)
                .roomNo("201A")
                .price(BigDecimal.valueOf(-1))
                .beds(List.of(BedSize.SINGLE))
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


        mockMvc.perform(post(RestAPIRoutes.CREATE_ROOM)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithBadRequestWhenCreatingRoomWithNullPrice() throws Exception {
        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        CreateRoomRequest request = CreateRoomRequest.builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(3)
                .roomNo("201A")
                .price(null)
                .beds(List.of(BedSize.SINGLE))
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


        mockMvc.perform(post(RestAPIRoutes.CREATE_ROOM)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
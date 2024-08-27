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
import com.tinqinacademy.bff.api.operations.deleteroom.DeleteRoomRequest;
import com.tinqinacademy.bff.api.operations.partialupdateroom.PartialUpdateRoomRequest;
import com.tinqinacademy.bff.api.operations.updateroom.UpdateRoomRequest;
import com.tinqinacademy.hotel.api.RestAPIRoutes;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomOutput;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void shouldRespondWithOKAndRoomIdWhenUpdatingRoom() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(3)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .roomId(UUID.randomUUID())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.updateRoom(any(), any())).thenReturn(output);

        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(output.getRoomId().toString()));

    }

    @Test
    void shouldRespondWithBadRequestWhenUpdatingRoomWithInvalidRoomId() throws Exception {
        String roomId = "invalid";

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

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(3)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldRespondWithBadRequestWhenUpdatingRoomWithNullBathroomType() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(null)
                .floor(3)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .roomId(UUID.randomUUID())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.updateRoom(any(), any())).thenReturn(output);

        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithBadRequestWhenUpdatingRoomWithNullFloor() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(null)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .roomId(UUID.randomUUID())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.updateRoom(any(), any())).thenReturn(output);

        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldRespondWithBadRequestWhenUpdatingRoomWithAboveMaxFloor() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(11)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .roomId(UUID.randomUUID())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.updateRoom(any(), any())).thenReturn(output);

        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldRespondWithBadRequestWhenUpdatingRoomWithBelowMinFloor() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(0)
                .roomNo("201A")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .roomId(UUID.randomUUID())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.updateRoom(any(), any())).thenReturn(output);

        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldRespondWithBadRequestWhenUpdatingRoomWithNullRoomNo() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(2)
                .roomNo(null)
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .roomId(UUID.randomUUID())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.updateRoom(any(), any())).thenReturn(output);

        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldRespondWithBadRequestWhenUpdatingRoomWithInvalidRoomNo() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(2)
                .roomNo("201")
                .price(BigDecimal.valueOf(20))
                .beds(List.of(BedSize.SINGLE))
                .build();

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .roomId(UUID.randomUUID())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.updateRoom(any(), any())).thenReturn(output);

        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldRespondWithBadRequestWhenUpdatingRoomWithNullPrice() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(2)
                .roomNo("201")
                .price(null)
                .beds(List.of(BedSize.SINGLE))
                .build();

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .roomId(UUID.randomUUID())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.updateRoom(any(), any())).thenReturn(output);

        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldRespondWithBadRequestWhenUpdatingRoomWithNegativePrice() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(2)
                .roomNo("201")
                .price(BigDecimal.valueOf(-1))
                .beds(List.of(BedSize.SINGLE))
                .build();

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .roomId(UUID.randomUUID())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.updateRoom(any(), any())).thenReturn(output);

        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldRespondWithUnauthorizedWhenUpdatingRoomWithoutAuthentication() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(2)
                .roomNo("201")
                .price(BigDecimal.valueOf(-1))
                .beds(List.of(BedSize.SINGLE))
                .build();


        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void shouldRespondWithForbiddenWhenUpdatingRoomWithInsufficientRights() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        UpdateRoomRequest request = UpdateRoomRequest
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .floor(2)
                .roomNo("201")
                .price(BigDecimal.valueOf(-1))
                .beds(List.of(BedSize.SINGLE))
                .build();

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .roomId(UUID.randomUUID())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.updateRoom(any(), any())).thenReturn(output);

        mockMvc.perform(put(RestAPIRoutes.UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

    }

    @Test
    void shouldRespondWithOKWhenDeletingRoom() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        DeleteRoomRequest request = DeleteRoomRequest
                .builder()
                .roomId(roomId)
                .build();

        DeleteRoomOutput expectedOutput = DeleteRoomOutput
                .builder()
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.deleteRoom(request.getRoomId())).thenReturn(expectedOutput);

        mockMvc.perform(delete(RestAPIRoutes.DELETE_ROOM, request.getRoomId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

    }

    @Test
    void shouldRespondWithBadRequestWhenDeletingRoomWithInvalidId() throws Exception {
        String roomId = "invalid";

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

        DeleteRoomRequest request = DeleteRoomRequest
                .builder()
                .roomId(roomId)
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(delete(RestAPIRoutes.DELETE_ROOM, request.getRoomId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field roomId must be UUID"));;

    }

    @Test
    void shouldRespondWithForbiddenWhenDeletingRoomWithInsufficientRights() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        DeleteRoomRequest request = DeleteRoomRequest
                .builder()
                .roomId(roomId)
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(delete(RestAPIRoutes.DELETE_ROOM, request.getRoomId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

    }

    @Test
    void shouldRespondWithUnauthorizedWhenDeletingRoomWithoutAuthentication() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        DeleteRoomRequest request = DeleteRoomRequest
                .builder()
                .roomId(roomId)
                .build();

        mockMvc.perform(delete(RestAPIRoutes.DELETE_ROOM, request.getRoomId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void shouldRespondWithOKAndRoomIdWhenPartiallyUpdatingRoom() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        PartialUpdateRoomRequest request = PartialUpdateRoomRequest
                .builder()
                .roomNo("301A")
                .price(BigDecimal.valueOf(20))
                .floor(3)
                .build();

        PartialUpdateRoomOutput expectedOutput = PartialUpdateRoomOutput
                .builder()
                .roomId(UUID.fromString(roomId))
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.partialUpdateRoom(any(), any())).thenReturn(expectedOutput);

        mockMvc.perform(patch(RestAPIRoutes.PARTIAL_UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(roomId));
    }

    @Test
    void shouldRespondWithBadRequestWhenPartiallyUpdatingRoomWithInvalidRoomId() throws Exception {
        String roomId = "invalid";

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

        PartialUpdateRoomRequest request = PartialUpdateRoomRequest
                .builder()
                .roomNo("301A")
                .price(BigDecimal.valueOf(20))
                .floor(3)
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(patch(RestAPIRoutes.PARTIAL_UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field roomId must be UUID"));
    }

    @Test
    void shouldRespondWithBadRequestWhenPartiallyUpdatingRoomWithBelowMinFloor() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        PartialUpdateRoomRequest request = PartialUpdateRoomRequest
                .builder()
                .roomNo("301A")
                .price(BigDecimal.valueOf(20))
                .floor(0)
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(patch(RestAPIRoutes.PARTIAL_UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field floor must be minimum 1"));
    }

    @Test
    void shouldRespondWithBadRequestWhenPartiallyUpdatingRoomWithAboveMaxFloor() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        PartialUpdateRoomRequest request = PartialUpdateRoomRequest
                .builder()
                .roomNo("301A")
                .price(BigDecimal.valueOf(20))
                .floor(11)
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(patch(RestAPIRoutes.PARTIAL_UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field floor must be maximum 10"));
    }

    @Test
    void shouldRespondWithBadRequestWhenPartiallyUpdatingRoomWithInvalidRoomNoFormat() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        PartialUpdateRoomRequest request = PartialUpdateRoomRequest
                .builder()
                .roomNo("301")
                .price(BigDecimal.valueOf(20))
                .floor(7)
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(patch(RestAPIRoutes.PARTIAL_UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field roomNo must be 4 characters"));
    }

    @Test
    void shouldRespondWithBadRequestWhenPartiallyUpdatingRoomWithNegativePrice() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        PartialUpdateRoomRequest request = PartialUpdateRoomRequest
                .builder()
                .roomNo("301A")
                .price(BigDecimal.valueOf(-1))
                .floor(7)
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(patch(RestAPIRoutes.PARTIAL_UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field price must be min 0"));
    }

    @Test
    void shouldRespondWithForbiddenWhenPartiallyUpdatingRoomWithInsufficientRights() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        PartialUpdateRoomRequest request = PartialUpdateRoomRequest
                .builder()
                .roomNo("301A")
                .price(BigDecimal.valueOf(20))
                .floor(7)
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(patch(RestAPIRoutes.PARTIAL_UPDATE_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRespondWithUnauthorizedWhenPartiallyUpdatingRoomWithoutAuthentication() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        PartialUpdateRoomRequest request = PartialUpdateRoomRequest
                .builder()
                .roomNo("301A")
                .price(BigDecimal.valueOf(20))
                .floor(7)
                .build();

        mockMvc.perform(patch(RestAPIRoutes.PARTIAL_UPDATE_ROOM, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

}
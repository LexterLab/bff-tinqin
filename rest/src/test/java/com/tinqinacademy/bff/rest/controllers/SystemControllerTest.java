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
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReportRequest;
import com.tinqinacademy.bff.api.operations.partialupdateroom.PartialUpdateRoomRequest;
import com.tinqinacademy.bff.api.operations.registerguest.GuestInput;
import com.tinqinacademy.bff.api.operations.registerguest.RegisterGuestRequest;
import com.tinqinacademy.bff.api.operations.updateroom.UpdateRoomRequest;
import com.tinqinacademy.hotel.api.RestAPIRoutes;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.getguestreport.GetGuestReportOutput;
import com.tinqinacademy.hotel.api.operations.getguestreport.GuestOutput;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

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

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

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

    @Test
    void shouldRespondWithCreatedWhenRegisteringGuest() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(2))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithInvalidBookingId() throws Exception {
        String bookingId = "invalid";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(2))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field bookingId must be UUID"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithNullFirstName() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName(null)
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(2))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field firstName must not be empty"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithBelowMinFirstName() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("S")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(2))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field firstName must be between 2-20 characters"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithAboveMaxFirstName() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("SebastianSebastianSebastianSebastianSebastianSebastianSebastianSebastianSebastianSebastianSebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(2))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field firstName must be between 2-20 characters"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithAboveMaxLastName() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("VettelVettelVettelVettelVettelVettelVettelVettelVettelVettelVettelVettelVettelVettelVettelVettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(2))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field lastName must be between 2-20 characters"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithBelowMinLastName() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("V")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(2))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field lastName must be between 2-20 characters"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithInvalidBirthday() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().plusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(2))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field birthDay must be a past date"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithNullBirthday() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(null)
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(2))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field birthDay cannot be null"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithBlankIdCardNo() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo(" ")
                .idCardValidity(LocalDate.now().plusYears(2))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field idCardNo must not be empty"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithInvalidIdCardValidity() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().minusYears(2))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field idCardValidity must be valid"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithNullIdCardValidity() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(null)
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field idCardValidity should not be null"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithNullIdCardIssueAuthority() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(10))
                .idCardIssueAuthority(null)
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field idCardIssueAuthority must not be empty"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithBelowMinCardIssueAuthority() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(10))
                .idCardIssueAuthority("B")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field idCardIssueAuthority must be between 2-100 characters"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithAboveMaxCardIssueAuthority() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(10))
                .idCardIssueAuthority("BULGARISTANBULGARISTANBULGARISTANBULGARISTANBULGARISTANBULGARISTANBULGARISTANBULGARISTANBULGARISTANBULGARISTAN")
                .idCardIssueDate(LocalDate.now().minusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field idCardIssueAuthority must be between 2-100 characters"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithInvalidIdCardIssueDate() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(10))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().plusYears(6))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field idCardIssueDate must be past or present"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithNullIdCardIssueDate() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(10))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(null)
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field idCardIssueDate must not be null"));
    }

    @Test
    void shouldRespondWithBadRequestWhenRegisteringGuestWithInsufficientRights() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(10))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(1))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRespondWithUnauthorizedWhenRegisteringGuestWithoutAuthentication() throws Exception {
        String bookingId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        GuestInput guest = GuestInput
                .builder()
                .firstName("Sebastian")
                .lastName("Vettel")
                .birthday(LocalDate.now().minusYears(20))
                .idCardNo("3232 3232 3232 3232")
                .idCardValidity(LocalDate.now().plusYears(10))
                .idCardIssueAuthority("BGN")
                .idCardIssueDate(LocalDate.now().minusYears(1))
                .build();

        RegisterGuestRequest request = RegisterGuestRequest
                .builder()
                .guests(List.of(guest))
                .build();

        mockMvc.perform(post(RestAPIRoutes.REGISTER_VISITOR, bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRespondWithOKAndGuestReportsWhenSearchingGuests() throws Exception {
        GetGuestReportRequest request = GetGuestReportRequest
                .builder()
                .startDate(LocalDateTime.now().plusMonths(2))
                .firstName("George")
                .lastName("Russell")
                .build();

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


        GuestOutput guest = GuestOutput
                .builder()
                .firstName("George")
                .lastName("Russell")
                .userId(UUID.randomUUID())
                .build();

        GetGuestReportOutput expectedOutput = GetGuestReportOutput
                .builder()
                .guestsReports(List.of(guest))
                .build();


        when(authenticationClient.loadUser(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateAccessToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(hotelClient.getGuestReport(request.getStartDate(), request.getEndDate(), request.getFirstName(),
                request.getLastName(), UUID.randomUUID().toString(), request.getIdCardNo(), request.getIdCardValidity(),
                request.getIdCardIssueAuthority(), request.getIdCardIssueDate(), request.getRoomNo()))
                .thenReturn(expectedOutput);

        mockMvc.perform(get(RestAPIRoutes.GET_VISITORS_REPORT)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guestss[0].firstName").value(request.getFirstName()))
                .andExpect(jsonPath("$.guestss[0].lastName").value(request.getLastName()));
    }

}
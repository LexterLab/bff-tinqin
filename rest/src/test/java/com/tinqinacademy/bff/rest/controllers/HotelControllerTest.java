package com.tinqinacademy.bff.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.authentication.api.operations.generateaccesstoken.GetUsernameFromTokenOutput;
import com.tinqinacademy.authentication.api.operations.getuser.GetUserOutput;
import com.tinqinacademy.authentication.api.operations.loaduserdetails.LoadUserDetailsInput;
import com.tinqinacademy.authentication.api.operations.loaduserdetails.LoadUserDetailsOutput;
import com.tinqinacademy.authentication.api.operations.validateacesstoken.ValidateAccessTokenInput;
import com.tinqinacademy.authentication.api.operations.validateacesstoken.ValidateAccessTokenOutput;
import com.tinqinacademy.authentication.restexport.AuthenticationClient;
import com.tinqinacademy.bff.api.RestRoutes;
import com.tinqinacademy.bff.api.operations.bookroom.BookRoomRequest;
import com.tinqinacademy.comments.api.operations.getroomcomments.GetRoomCommentsOutput;
import com.tinqinacademy.comments.api.operations.getroomcomments.RoomCommentOutput;
import com.tinqinacademy.comments.restexport.restexport.CommentClient;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @MockBean
    private AuthenticationClient authenticationClient;

    @MockBean
    private CommentClient commentClient;

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
    void shouldRespondWithCreatedWhenBookingRoom() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        BookRoomRequest request = BookRoomRequest
                .builder()
                .startDate(LocalDateTime.now().plusMonths(1))
                .endDate(LocalDateTime.now().plusMonths(1).plusWeeks(1))
                .firstName("George")
                .lastName("Russell")
                .phoneNo("+35983323232434")
                .build();

        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        GetUserOutput getUserOutput = GetUserOutput
                .builder()
                .id(UUID.randomUUID())
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(authenticationClient.getUser(getUsernameFromTokenOutput.getUsername())).thenReturn(getUserOutput);

        mockMvc.perform(post(RestAPIRoutes.BOOK_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRespondWithBadRequestWhenBookingRoomWithInvalidId() throws Exception {
        String roomId = "invalid";

        BookRoomRequest request = BookRoomRequest
                .builder()
                .startDate(LocalDateTime.now().plusMonths(1))
                .endDate(LocalDateTime.now().plusMonths(1).plusWeeks(1))
                .firstName("George")
                .lastName("Russell")
                .phoneNo("+35983323232434")
                .build();

        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.BOOK_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithUnauthorizedWhenBookingRoomWithInvalidToken() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        BookRoomRequest request = BookRoomRequest
                .builder()
                .startDate(LocalDateTime.now().plusMonths(1))
                .endDate(LocalDateTime.now().plusMonths(1).plusWeeks(1))
                .firstName("George")
                .lastName("Russell")
                .phoneNo("+35983323232434")
                .build();

        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(false)
                .build();

        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.BOOK_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRespondWithForbiddenWhenBookingRoomWithInsufficientRole() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        BookRoomRequest request = BookRoomRequest
                .builder()
                .startDate(LocalDateTime.now().plusMonths(1))
                .endDate(LocalDateTime.now().plusMonths(1).plusWeeks(1))
                .firstName("George")
                .lastName("Russell")
                .phoneNo("+35983323232434")
                .build();

        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_NONE")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestAPIRoutes.BOOK_ROOM, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRespondWithOKAndRoomComments() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        RoomCommentOutput commentData = RoomCommentOutput
                .builder()
                .id(UUID.fromString(roomId))
                .content("Content")
                .firstName("George")
                .lastName("Russell")
                .publishDate(LocalDateTime.now())
                .build();

        GetRoomOutput roomOutput = GetRoomOutput
                .builder()
                .bedCount(1)
                .price(BigDecimal.valueOf(20000))
                .floor(4)
                .bedSizes(List.of(BedSize.SINGLE))
                .bathroomType(BathroomType.PRIVATE)
                .datesOccupied(List.of())
                .id(UUID.fromString(roomId))
                .build();

        GetRoomCommentsOutput expectedOutput = GetRoomCommentsOutput
                .builder()
                .roomComments(List.of(commentData))
                .build();

        when(hotelClient.getRoomById(roomId)).thenReturn(roomOutput);
        when(commentClient.getRoomComments(roomId)).thenReturn(expectedOutput);

        mockMvc.perform(get(RestRoutes.GET_ROOM_COMMENTS, roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomComments").isNotEmpty())
                .andExpect(jsonPath("$.roomComments").isArray())
                .andExpect(jsonPath("$.roomComments[0].id").value(expectedOutput.getRoomComments().getFirst().getId().toString()))
                .andExpect(jsonPath("$.roomComments[0].content").value(expectedOutput.getRoomComments().getFirst().getContent()))
                .andExpect(jsonPath("$.roomComments[0].firstName").value(expectedOutput.getRoomComments().getFirst().getFirstName()))
                .andExpect(jsonPath("$.roomComments[0].lastName").value(expectedOutput.getRoomComments().getFirst().getLastName()));
    }

    @Test
    void shouldRespondWithBadRequestAndRoomCommentsWhenProvidedInvalidRoomId() throws Exception {
        String roomId = "invalid";

        mockMvc.perform(get(RestRoutes.GET_ROOM_COMMENTS, roomId))
                .andExpect(status().isBadRequest());
    }
}
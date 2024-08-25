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
import com.tinqinacademy.bff.api.operations.editcomment.EditCommentRequest;
import com.tinqinacademy.bff.api.operations.leaveroomcomment.LeaveRoomCommentRequest;
import com.tinqinacademy.bff.api.operations.searchroom.SearchRoomRequest;
import com.tinqinacademy.comments.api.operations.editcomment.EditCommentInput;
import com.tinqinacademy.comments.api.operations.editcomment.EditCommentOutput;
import com.tinqinacademy.comments.api.operations.getroomcomments.GetRoomCommentsOutput;
import com.tinqinacademy.comments.api.operations.getroomcomments.RoomCommentOutput;
import com.tinqinacademy.comments.api.operations.leaveroomcomment.LeaveRoomCommentOutput;
import com.tinqinacademy.comments.restexport.restexport.CommentClient;
import com.tinqinacademy.hotel.api.RestAPIRoutes;
import com.tinqinacademy.hotel.api.enumerations.BathroomType;
import com.tinqinacademy.hotel.api.enumerations.BedSize;
import com.tinqinacademy.hotel.api.operations.getroom.GetRoomOutput;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoomInput;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoomOutput;
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
    void shouldRespondWithBadRequestWhenProvidedInvalidRoomIdWhenRetrievingComments() throws Exception {
        String roomId = "invalid";

        mockMvc.perform(get(RestRoutes.GET_ROOM_COMMENTS, roomId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithCreatedAndRoomCommentIdWhenLeavingComment() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        LeaveRoomCommentRequest request = LeaveRoomCommentRequest.builder()
                .roomId(roomId)
                .content("content")
                .firstName("George")
                .lastName("Russell")
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

        LeaveRoomCommentOutput expectedOutput = LeaveRoomCommentOutput.builder()
                .id(UUID.randomUUID())
                .build();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(authenticationClient.getUser(getUsernameFromTokenOutput.getUsername())).thenReturn(getUserOutput);
        when(hotelClient.getRoomById(roomId)).thenReturn(roomOutput);
        when(commentClient.leaveRoomComment(any(), any())).thenReturn(expectedOutput);

        mockMvc.perform(post(RestRoutes.LEAVE_COMMENT, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedOutput.getId().toString()));

    }

    @Test
    void shouldRespondWithBadRequestWhenLeavingCommentWithInvalidRoomId() throws Exception {
        String roomId = "invalid";

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

        LeaveRoomCommentRequest request = LeaveRoomCommentRequest.builder()
                .roomId(roomId)
                .content("content")
                .firstName("George")
                .lastName("Russell")
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestRoutes.LEAVE_COMMENT, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field roomId must be UUID"));
    }

    @Test
    void shouldRespondWithUnAuthorizedWhenLeavingCommentWithoutBeingAuthenticated() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";


        LeaveRoomCommentRequest request = LeaveRoomCommentRequest.builder()
                .roomId(roomId)
                .content("content")
                .firstName("George")
                .lastName("Russell")
                .build();

        mockMvc.perform(post(RestRoutes.LEAVE_COMMENT, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRespondWithForbiddenWhenLeavingCommentWithInsufficientRights() throws Exception {
        String roomId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_NONE")
                .build();


        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        LeaveRoomCommentRequest request = LeaveRoomCommentRequest.builder()
                .roomId(roomId)
                .content("content")
                .firstName("George")
                .lastName("Russell")
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);


        mockMvc.perform(post(RestRoutes.LEAVE_COMMENT, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRespondWithBadRequestWhenLeavingCommentWithBlankContent() throws Exception {
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

        LeaveRoomCommentRequest request = LeaveRoomCommentRequest.builder()
                .roomId(roomId)
                .content(null)
                .firstName("George")
                .lastName("Russell")
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestRoutes.LEAVE_COMMENT, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field content cannot not be blank"));
    }

    @Test
    void shouldRespondWithBadRequestWhenLeavingCommentWithUnderMinContent() throws Exception {
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

        LeaveRoomCommentRequest request = LeaveRoomCommentRequest.builder()
                .roomId(roomId)
                .content("bro")
                .firstName("George")
                .lastName("Russell")
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestRoutes.LEAVE_COMMENT, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field content must be between 5-500 characters"));
    }

    @Test
    void shouldRespondWithBadRequestWhenLeavingCommentWithOverMaxContent() throws Exception {
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

        LeaveRoomCommentRequest request = LeaveRoomCommentRequest.builder()
                .roomId(roomId)
                .content("""
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer rhoncus fermentum eros, sed vehicula eros commodo nec. Nunc nec viverra nunc. Sed nec nisl eu neque viverra laoreet. Maecenas sed posuere leo. Quisque eget facilisis lectus, nec bibendum felis. Etiam efficitur nisi a nisl bibendum suscipit. Praesent vitae tortor odio. Aenean bibendum odio pellentesque dui accumsan, nec malesuada augue consequat. Phasellus mattis non ex efficitur blandit. Vivamus dapibus lacus et rutrum commodo. In hac habitasse platea dictumst. Nam pellentesque a lacus a laoreet. Vivamus at blandit risus. Nunc elementum sodales elit, at fermentum erat aliquet placerat. Curabitur sit amet consectetur risus. Aenean vestibulum finibus ligula sit amet placerat.
                        Nullam interdum nisi non nisl laoreet, non egestas ex efficitur. Aliquam quis metus viverra, dictum lorem at, dignissim leo. Quisque at diam rhoncus, volutpat massa quis, blandit risus. Vivamus ut massa varius, ultricies arcu aliquam, finibus ligula. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Donec malesuada lobortis massa mattis malesuada. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.
                        Fusce vestibulum massa vitae risus pulvinar, ut sollicitudin lacus iaculis. Proin dapibus quam vitae nibh iaculis molestie. Etiam ut tellus fringilla, bibendum eros in, ultricies augue. Fusce a velit eget ex consequat convallis. Vestibulum hendrerit faucibus dignissim. Vestibulum molestie ullamcorper sollicitudin. Nullam ut ultricies sem. Integer lorem purus, dictum vel nulla a, facilisis porttitor nisl. Suspendisse ullamcorper ligula tellus, sed ornare erat rhoncus nec. Vestibulum bibendum lacus quis dui hendrerit lobortis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Cras id tellus viverra, euismod augue a, lacinia mauris. Etiam auctor tempus est nec commodo.
                        Quisque posuere sem nunc, ut rutrum lorem imperdiet non. In hac habitasse platea dictumst. Pellentesque ac nulla at dui imperdiet varius eget laoreet augue. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam rutrum facilisis iaculis. Pellentesque congue auctor mauris. Donec porta luctus velit semper pretium.
                        Fusce et purus ac massa ornare condimentum. Sed at ante a felis molestie scelerisque. Cras venenatis ultricies enim a bibendum. Duis tincidunt malesuada rhoncus. Morbi id vehicula ipsum. Proin porta fermentum nunc, eget porta nulla aliquam vel. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tortor nisi, efficitur nec dolor quis, facilisis semper tellus.
                        Proin ut libero vel ante maximus imperdiet at iaculis odio. Vestibulum semper volutpat augue, vel congue orci rutrum eget. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec placerat malesuada risus at aliquet. Vivamus nec lectus cursus, mattis ipsum non, mollis justo. Aliquam mollis tristique pretium. Quisque quis urna id dui vulputate gravida sit amet vel tellus. Duis mattis, nunc a tincidunt aliquet, elit ex dictum erat, sed consequat mauris nisl et tortor. Cras tempus nulla quam, non porta nulla varius consectetur. Aliquam sed facilisis mauris, non porttitor tortor. Proin bibendum in risus accumsan vehicula. Maecenas neque urna, sagittis eget tellus ac, egestas rutrum elit. Fusce non varius tellus, semper rhoncus augue. Curabitur ut rhoncus nibh, eget semper tellus. Etiam.
                        """)
                .firstName("George")
                .lastName("Russell")
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(post(RestRoutes.LEAVE_COMMENT, roomId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field content must be between 5-500 characters"));
    }

    @Test
    void shouldRespondWithOKAndCommentIdWhenEditingPersonalRoomComment() throws Exception {
        String commentId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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


        GetUserOutput getUserOutput = GetUserOutput
                .builder()
                .id(UUID.randomUUID())
                .build();

        EditCommentRequest request = EditCommentRequest
                .builder()
                .content("content")
                .build();

        EditCommentOutput output = EditCommentOutput
                .builder()
                .id(UUID.fromString(commentId))
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(authenticationClient.getUser(getUsernameFromTokenOutput.getUsername())).thenReturn(getUserOutput);
        when(commentClient.editComment(any(), any())).thenReturn(output);

        mockMvc.perform(patch(RestRoutes.EDIT_COMMENT, commentId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId));
    }

    @Test
    void shouldRespondWithBadRequestWhenEditingPersonalRoomCommentWithInvalidCommentIdFormat() throws Exception {
        String commentId = "invalid-comment-id";

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

        EditCommentRequest request = EditCommentRequest
                .builder()
                .content("content")
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(patch(RestRoutes.EDIT_COMMENT, commentId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field id must be UUID"));
    }

    @Test
    void shouldRespondWithBadRequestWhenEditingPersonalRoomCommentWithNullContent() throws Exception {
        String commentId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        EditCommentRequest request = EditCommentRequest
                .builder()
                .content(null)
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(patch(RestRoutes.EDIT_COMMENT, commentId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field content cannot be blank"));
    }

    @Test
    void shouldRespondWithBadRequestWhenEditingPersonalRoomCommentWithAboveMaxContent() throws Exception {
        String commentId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        EditCommentRequest request = EditCommentRequest
                .builder()
                .content("""
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer rhoncus fermentum eros, sed vehicula eros commodo nec. Nunc nec viverra nunc. Sed nec nisl eu neque viverra laoreet. Maecenas sed posuere leo. Quisque eget facilisis lectus, nec bibendum felis. Etiam efficitur nisi a nisl bibendum suscipit. Praesent vitae tortor odio. Aenean bibendum odio pellentesque dui accumsan, nec malesuada augue consequat. Phasellus mattis non ex efficitur blandit. Vivamus dapibus lacus et rutrum commodo. In hac habitasse platea dictumst. Nam pellentesque a lacus a laoreet. Vivamus at blandit risus. Nunc elementum sodales elit, at fermentum erat aliquet placerat. Curabitur sit amet consectetur risus. Aenean vestibulum finibus ligula sit amet placerat.
                        Nullam interdum nisi non nisl laoreet, non egestas ex efficitur. Aliquam quis metus viverra, dictum lorem at, dignissim leo. Quisque at diam rhoncus, volutpat massa quis, blandit risus. Vivamus ut massa varius, ultricies arcu aliquam, finibus ligula. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Donec malesuada lobortis massa mattis malesuada. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.
                        Fusce vestibulum massa vitae risus pulvinar, ut sollicitudin lacus iaculis. Proin dapibus quam vitae nibh iaculis molestie. Etiam ut tellus fringilla, bibendum eros in, ultricies augue. Fusce a velit eget ex consequat convallis. Vestibulum hendrerit faucibus dignissim. Vestibulum molestie ullamcorper sollicitudin. Nullam ut ultricies sem. Integer lorem purus, dictum vel nulla a, facilisis porttitor nisl. Suspendisse ullamcorper ligula tellus, sed ornare erat rhoncus nec. Vestibulum bibendum lacus quis dui hendrerit lobortis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Cras id tellus viverra, euismod augue a, lacinia mauris. Etiam auctor tempus est nec commodo.
                        Quisque posuere sem nunc, ut rutrum lorem imperdiet non. In hac habitasse platea dictumst. Pellentesque ac nulla at dui imperdiet varius eget laoreet augue. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam rutrum facilisis iaculis. Pellentesque congue auctor mauris. Donec porta luctus velit semper pretium.
                        Fusce et purus ac massa ornare condimentum. Sed at ante a felis molestie scelerisque. Cras venenatis ultricies enim a bibendum. Duis tincidunt malesuada rhoncus. Morbi id vehicula ipsum. Proin porta fermentum nunc, eget porta nulla aliquam vel. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tortor nisi, efficitur nec dolor quis, facilisis semper tellus.
                        Proin ut libero vel ante maximus imperdiet at iaculis odio. Vestibulum semper volutpat augue, vel congue orci rutrum eget. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec placerat malesuada risus at aliquet. Vivamus nec lectus cursus, mattis ipsum non, mollis justo. Aliquam mollis tristique pretium. Quisque quis urna id dui vulputate gravida sit amet vel tellus. Duis mattis, nunc a tincidunt aliquet, elit ex dictum erat, sed consequat mauris nisl et tortor. Cras tempus nulla quam, non porta nulla varius consectetur. Aliquam sed facilisis mauris, non porttitor tortor. Proin bibendum in risus accumsan vehicula. Maecenas neque urna, sagittis eget tellus ac, egestas rutrum elit. Fusce non varius tellus, semper rhoncus augue. Curabitur ut rhoncus nibh, eget semper tellus. Etiam.
                        """)
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(patch(RestRoutes.EDIT_COMMENT, commentId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Field content must be 5-500 characters"));
    }

    @Test
    void shouldRespondWithBadRequestWhenEditingPersonalRoomCommentWithBelowMinContent() throws Exception {
        String commentId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

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

        EditCommentRequest request = EditCommentRequest
                .builder()
                .content("yo")
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(patch(RestRoutes.EDIT_COMMENT, commentId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Field content must be 5-500 characters"));
    }

    @Test
    void shouldRespondWithUnauthorizedWhenEditingPersonalRoomCommentWithoutAuthentication() throws Exception {
        String commentId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        EditCommentRequest request = EditCommentRequest
                .builder()
                .content("content")
                .build();


        mockMvc.perform(patch(RestRoutes.EDIT_COMMENT, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRespondWithForbiddenWhenEditingPersonalRoomCommentWithInsufficientRights() throws Exception {
        String commentId = "923364b0-4ed0-4a7e-8c23-ceb5c238ceee";

        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_NONE")
                .build();


        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        EditCommentRequest request = EditCommentRequest
                .builder()
                .content("content")
                .build();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);

        mockMvc.perform(patch(RestRoutes.EDIT_COMMENT, commentId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRespondWithOKAndAvailableRoomIds() throws Exception {
        SearchRoomInput input = SearchRoomInput
                .builder()
                .bathroomType(BathroomType.PRIVATE)
                .bedSize(BedSize.SINGLE)
                .startDate(LocalDateTime.now().plusMonths(1))
                .endDate(LocalDateTime.now().plusMonths(1).plusWeeks(1))
                .bedCount(1)
                .build();

        SearchRoomOutput expectedOutput = SearchRoomOutput
                .builder()
                .roomIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .build();

        when(hotelClient.searchRooms(input.getStartDate(),input.getEndDate(), input.getBedCount(),
                input.getBedSize().toString(), input.getBathroomType().toString()))
                .thenReturn(expectedOutput);

        mockMvc.perform(get(RestAPIRoutes.SEARCH_ROOMS)
                .param("startDate", String.valueOf(input.getStartDate()))
                .param("endDate", String.valueOf(input.getEndDate()))
                .param("bedCount", String.valueOf(input.getBedCount()))
                .param("bedSize", input.getBedSize().toString())
                .param("bathroomType", input.getBathroomType().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomIds").isArray())
                .andExpect(jsonPath("$.roomIds[0]").value(expectedOutput.getRoomIds().getFirst().toString()))
                .andExpect(jsonPath("$.roomIds[1]").value(expectedOutput.getRoomIds().getLast().toString()));

    }

    @Test
    void shouldRespondWithOKWhenCancellingRoomBooking() throws Exception {
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


        GetUserOutput getUserOutput = GetUserOutput
                .builder()
                .id(UUID.randomUUID())
                .build();

        String bookingId = UUID.randomUUID().toString();


        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(authenticationClient.getUser(getUsernameFromTokenOutput.getUsername())).thenReturn(getUserOutput);

        mockMvc.perform(delete(RestAPIRoutes.UNBOOK_ROOM, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRespondWithBadRequestWhenCancellingRoomBookingWithInvalidBookingIdFormat() throws Exception {
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

        GetUserOutput getUserOutput = GetUserOutput
                .builder()
                .id(UUID.randomUUID())
                .build();

        String bookingId = "invalid";

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(authenticationClient.getUser(getUsernameFromTokenOutput.getUsername())).thenReturn(getUserOutput);

        mockMvc.perform(delete(RestAPIRoutes.UNBOOK_ROOM, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("Field roomId must be UUID"));
    }

    @Test
    void shouldRespondWithForbiddenWhenCancellingRoomBookingWithInsufficientRights() throws Exception {
        String accessToken = "token";

        User userDetails = (User) User.withUsername("domino222")
                .password("password")
                .authorities("ROLE_NONE")
                .build();

        GetUsernameFromTokenOutput getUsernameFromTokenOutput = GetUsernameFromTokenOutput.builder()
                .username("domino222")
                .build();

        ValidateAccessTokenOutput validateAccessTokenOutput = ValidateAccessTokenOutput.builder()
                .success(true)
                .build();

        GetUserOutput getUserOutput = GetUserOutput
                .builder()
                .id(UUID.randomUUID())
                .build();

        String bookingId = UUID.randomUUID().toString();

        when(authenticationClient.loadUserDetails(any(LoadUserDetailsInput.class)))
                .thenReturn(LoadUserDetailsOutput.builder().userDetails(userDetails).build());
        when(authenticationClient.getUsernameFromToken(any())).thenReturn(getUsernameFromTokenOutput);
        when(authenticationClient.validateToken(any(ValidateAccessTokenInput.class))).thenReturn(validateAccessTokenOutput);
        when(authenticationClient.getUser(getUsernameFromTokenOutput.getUsername())).thenReturn(getUserOutput);

        mockMvc.perform(delete(RestAPIRoutes.UNBOOK_ROOM, bookingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRespondWithUnauthorizedWhenCancellingRoomBookingWithoutAuthentication() throws Exception {
        String bookingId = UUID.randomUUID().toString();

        mockMvc.perform(delete(RestAPIRoutes.UNBOOK_ROOM, bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
package com.tinqinacademy.bff.rest.controllers;

import com.tinqinacademy.bff.api.createroom.CreateRoom;
import com.tinqinacademy.bff.api.createroom.CreateRoomRequest;
import com.tinqinacademy.bff.api.createroom.CreateRoomResponse;
import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.RestAPIRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "System REST APIs")
public class SystemController  extends BaseController {

    private final CreateRoom createRoom;


    @Operation(
            summary = "Create Room Rest API",
            description = "Create Room Rest API is for searching visitor registrations"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "HTTP STATUS 201 CREATED"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS 400 BAD REQUEST"),
            @ApiResponse(responseCode = "403", description = "HTTP STATUS 403 FORBIDDEN"),
    })
    @PostMapping(RestAPIRoutes.CREATE_ROOM)
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest input) {
        Either<ErrorOutput, CreateRoomResponse> result = createRoom.process(input);
        return handleOutput(result, HttpStatus.CREATED);
    }
}

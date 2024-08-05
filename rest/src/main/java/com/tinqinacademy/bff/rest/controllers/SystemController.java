package com.tinqinacademy.bff.rest.controllers;

import com.tinqinacademy.hotel.api.RestAPIRoutes;
import com.tinqinacademy.hotel.api.enumerations.BathroomType;
import com.tinqinacademy.hotel.api.enumerations.BedSize;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoom;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoom;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoom;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoomInput;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoomOutput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoom;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Tag(name = "System REST APIs")
public class SystemController  extends BaseController {

    private final CreateRoom createRoom;
    private final UpdateRoom updateRoom;
    private final DeleteRoom deleteRoom;
    private final SearchRoom searchRoom;

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
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomInput input) {
        Either<ErrorOutput, CreateRoomOutput> result = createRoom.process(input);
        return handleOutput(result, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update Room Rest API",
            description = "Update Room Rest API is for updating rooms"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "HTTP STATUS 200 SUCCESS"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS 400 BAD REQUEST"),
            @ApiResponse(responseCode = "403", description = "HTTP STATUS 403 FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS 404 NOT FOUND"),
    })
    @PutMapping(RestAPIRoutes.UPDATE_ROOM)
    public ResponseEntity<?> updateRoom(@PathVariable String roomId, @RequestBody UpdateRoomInput input) {
        Either<ErrorOutput, UpdateRoomOutput> output = updateRoom.process(UpdateRoomInput.builder()
                .roomId(roomId)
                .bathroomType(input.getBathroomType())
                .floor(input.getFloor())
                .beds(input.getBeds())
                .roomNo(input.getRoomNo())
                .price(input.getPrice())
                .build());
        return handleOutput(output, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete Room Rest API",
            description = "Delete Room Rest API is for deleting rooms"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "HTTP STATUS 200 SUCCESS"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS 400 BAD REQUEST"),
            @ApiResponse(responseCode = "403", description = "HTTP STATUS 403 FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS 404 NOT FOUND")
    })
    @DeleteMapping(RestAPIRoutes.DELETE_ROOM)
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {
        Either<ErrorOutput, DeleteRoomOutput> output = deleteRoom.process(DeleteRoomInput.builder().roomId(roomId).build());
        return handleOutput(output, HttpStatus.OK);
    }

    @Operation(
            summary = "Search Rooms Rest API",
            description = "Search Rooms Rest API is used for searching rooms"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "HTTP STATUS 200 SUCCESS"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS 400 BAD REQUEST")
    }
    )
    @GetMapping(RestAPIRoutes.SEARCH_ROOMS)
    public ResponseEntity<?> searchRooms(
            @RequestParam() LocalDateTime startDate,
            @RequestParam() LocalDateTime endDate,
            @RequestParam(required = false) Integer bedCount,
            @RequestParam(required = false) String bedSize,
            @RequestParam(required = false) String bathroomType
    ) {
        Either<ErrorOutput, SearchRoomOutput> output = searchRoom.process(
                SearchRoomInput.builder()
                        .bathroomType(BathroomType.getByCode(bathroomType))
                        .bedSize(BedSize.getByCode(bedSize))
                        .endDate(endDate)
                        .startDate(startDate)
                        .bedCount(bedCount)
                        .build());
        return handleOutput(output, HttpStatus.OK);
    }
}

package com.tinqinacademy.bff.rest.controllers;

import com.tinqinacademy.hotel.api.RestAPIRoutes;
import com.tinqinacademy.hotel.api.enumerations.BathroomType;
import com.tinqinacademy.hotel.api.enumerations.BedSize;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.operations.getroom.GetRoom;
import com.tinqinacademy.hotel.api.operations.getroom.GetRoomInput;
import com.tinqinacademy.hotel.api.operations.getroom.GetRoomOutput;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoom;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoomInput;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoomOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Tag(name = "Hotel REST APIs")
@RequiredArgsConstructor
public class HotelController extends BaseController {
    private final SearchRoom searchRoom;
    private final GetRoom getRoom;

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

    @Operation(
            summary = "Get Room By Id Rest API",
            description = "Get Room By Id REST API is used for retrieving a room by id"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "HTTP STATUS 200 SUCCESS"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS 400 BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS 404 NOT FOUND")
    }
    )
    @GetMapping(RestAPIRoutes.GET_ROOM_DETAILS)
    public ResponseEntity<?> getRoomById(@PathVariable String roomId) {
        Either<ErrorOutput, GetRoomOutput> output = getRoom.process(GetRoomInput.builder()
                .roomId(roomId).build());
        return handleOutput(output, HttpStatus.OK);
    }
}

package com.tinqinacademy.bff.rest.controllers;

import com.tinqinacademy.bff.api.RestRoutes;
import com.tinqinacademy.bff.api.operations.bookroom.BookRoom;
import com.tinqinacademy.bff.api.operations.bookroom.BookRoomRequest;
import com.tinqinacademy.bff.api.operations.bookroom.BookRoomResponse;
import com.tinqinacademy.bff.api.operations.getroom.GetRoom;
import com.tinqinacademy.bff.api.operations.getroom.GetRoomRequest;
import com.tinqinacademy.bff.api.operations.getroom.GetRoomResponse;
import com.tinqinacademy.bff.api.operations.getroomcomments.GetRoomComments;
import com.tinqinacademy.bff.api.operations.getroomcomments.GetRoomCommentsRequest;
import com.tinqinacademy.bff.api.operations.getroomcomments.GetRoomCommentsResponse;
import com.tinqinacademy.bff.api.operations.searchroom.SearchRoom;
import com.tinqinacademy.bff.api.operations.searchroom.SearchRoomRequest;
import com.tinqinacademy.bff.api.operations.searchroom.SearchRoomResponse;
import com.tinqinacademy.bff.api.operations.unbookroom.UnbookRoom;
import com.tinqinacademy.bff.api.operations.unbookroom.UnbookRoomRequest;
import com.tinqinacademy.bff.api.operations.unbookroom.UnbookRoomResponse;
import com.tinqinacademy.hotel.api.RestAPIRoutes;
import com.tinqinacademy.bff.api.enumerations.BathroomType;
import com.tinqinacademy.bff.api.enumerations.BedSize;
import com.tinqinacademy.bff.api.errors.ErrorOutput;
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
@Tag(name = "Hotel REST APIs")
@RequiredArgsConstructor
public class HotelController extends BaseController {
    private final SearchRoom searchRoom;
    private final GetRoom getRoom;
    private final BookRoom bookRoom;
    private final UnbookRoom unbookRoom;
    private final GetRoomComments getRoomComments;

    @GetMapping(RestAPIRoutes.SEARCH_ROOMS)
    public ResponseEntity<?> searchRooms(
            @RequestParam() LocalDateTime startDate,
            @RequestParam() LocalDateTime endDate,
            @RequestParam(required = false) Integer bedCount,
            @RequestParam(required = false) String bedSize,
            @RequestParam(required = false) String bathroomType
    ) {
        Either<ErrorOutput, SearchRoomResponse> output = searchRoom.process(
                SearchRoomRequest.builder()
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
        Either<ErrorOutput, GetRoomResponse> output = getRoom.process(GetRoomRequest.builder()
                .roomId(roomId).build());
        return handleOutput(output, HttpStatus.OK);
    }

    @Operation(
            summary = "Book Room By Id Rest API",
            description = "Book Room By Id REST API is used for booking a room by id"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "HTTP STATUS 201 CREATED"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS 400 BAD REQUEST"),
            @ApiResponse(responseCode = "403", description = "HTTP STATUS 403 FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS 404 NOT FOUND")
    }
    )
    @PostMapping(RestAPIRoutes.BOOK_ROOM)
    public ResponseEntity<?> bookRoom(@PathVariable String roomId , @RequestBody BookRoomRequest request) {
        Either<ErrorOutput, BookRoomResponse> output = bookRoom.process(BookRoomRequest.builder()
                .roomId(roomId)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNo(request.getPhoneNo())
                .userId(request.getUserId())
                .build());

        return handleOutput(output, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Unbook Room By Id Rest API",
            description = "Unbook Room By Id REST API is used for unbooking a room by id"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "HTTP STATUS 200 SUCCESS"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS 400 BAD REQUEST"),
            @ApiResponse(responseCode = "403", description = "HTTP STATUS 403 FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS 404 NOT FOUND")
    }
    )
    @DeleteMapping(RestAPIRoutes.UNBOOK_ROOM)
    public ResponseEntity<?> unbookRoom(@PathVariable String roomId, @RequestBody UnbookRoomRequest request) {
        Either<ErrorOutput, UnbookRoomResponse>  output = unbookRoom.process(UnbookRoomRequest
                .builder()
                .roomId(roomId)
                .userId(request.getUserId())
                .build());
        return handleOutput(output, HttpStatus.OK);
    }


    @Operation(
            summary = "Get Room comments By Id Rest API",
            description = "Get Room comments By Id REST API is used for retrieving room comments from a room by id"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "HTTP STATUS 200 SUCCESS"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS 400 BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS 404 NOT FOUND")
    }
    )
    @GetMapping(RestRoutes.GET_ROOM_COMMENTS)
    public ResponseEntity<?> getRoomComments(@PathVariable String roomId) {
        Either<ErrorOutput, GetRoomCommentsResponse> output =  getRoomComments.process(GetRoomCommentsRequest
                .builder()
                        .roomId(roomId)
                .build());
        return handleOutput(output, HttpStatus.OK);
    }
}

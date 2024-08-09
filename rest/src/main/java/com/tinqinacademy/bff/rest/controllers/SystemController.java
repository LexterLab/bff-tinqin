package com.tinqinacademy.bff.rest.controllers;

import com.tinqinacademy.bff.api.operations.createroom.CreateRoom;
import com.tinqinacademy.bff.api.operations.createroom.CreateRoomRequest;
import com.tinqinacademy.bff.api.operations.createroom.CreateRoomResponse;
import com.tinqinacademy.bff.api.operations.deleteroom.DeleteRoom;
import com.tinqinacademy.bff.api.operations.deleteroom.DeleteRoomRequest;
import com.tinqinacademy.bff.api.operations.deleteroom.DeleteRoomResponse;
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReport;
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReportRequest;
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReportResponse;
import com.tinqinacademy.bff.api.operations.partialupdateroom.PartialUpdateRoom;
import com.tinqinacademy.bff.api.operations.partialupdateroom.PartialUpdateRoomRequest;
import com.tinqinacademy.bff.api.operations.partialupdateroom.PartialUpdateRoomResponse;
import com.tinqinacademy.bff.api.operations.registerguest.RegisterGuest;
import com.tinqinacademy.bff.api.operations.registerguest.RegisterGuestRequest;
import com.tinqinacademy.bff.api.operations.registerguest.RegisterGuestResponse;
import com.tinqinacademy.bff.api.operations.updateroom.UpdateRoom;
import com.tinqinacademy.bff.api.operations.updateroom.UpdateRoomRequest;
import com.tinqinacademy.bff.api.operations.updateroom.UpdateRoomResponse;
import com.tinqinacademy.hotel.api.RestAPIRoutes;
import com.tinqinacademy.bff.api.errors.ErrorOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Tag(name = "System REST APIs")
public class SystemController  extends BaseController {

    private final CreateRoom createRoom;
    private final UpdateRoom updateRoom;
    private final DeleteRoom deleteRoom;
    private final PartialUpdateRoom partialUpdateRoom;
    private final RegisterGuest registerGuest;
    private final GetGuestReport getGuestReport;

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
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request) {
        Either<ErrorOutput, CreateRoomResponse> result = createRoom.process(request);
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
    public ResponseEntity<?> updateRoom(@PathVariable String roomId, @RequestBody UpdateRoomRequest request) {
        Either<ErrorOutput, UpdateRoomResponse> output = updateRoom.process(UpdateRoomRequest.builder()
                .roomId(roomId)
                .bathroomType(request.getBathroomType())
                .floor(request.getFloor())
                .beds(request.getBeds())
                .roomNo(request.getRoomNo())
                .price(request.getPrice())
                .build());
        return handleOutput(output, HttpStatus.OK);
    }

    @Operation(
            summary = "Partial Update Room Rest API",
            description = "Partial Update Room Rest API is for partially updating rooms"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "HTTP STATUS 200 SUCCESS"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS 400 BAD REQUEST"),
            @ApiResponse(responseCode = "403", description = "HTTP STATUS 403 FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS 404 NOT FOUND"),
    })
    @PatchMapping(RestAPIRoutes.PARTIAL_UPDATE_ROOM)
    public ResponseEntity<?> partialUpdateRoom(@PathVariable String roomId, @RequestBody PartialUpdateRoomRequest request) {
        Either<ErrorOutput, PartialUpdateRoomResponse> output = partialUpdateRoom.process(PartialUpdateRoomRequest.builder()
                .roomId(roomId)
                .beds(request.getBeds())
                .bathroomType(request.getBathroomType())
                .floor(request.getFloor())
                .roomNo(request.getRoomNo())
                .price(request.getPrice())
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
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @DeleteMapping(RestAPIRoutes.DELETE_ROOM)
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {
        Either<ErrorOutput, DeleteRoomResponse> output = deleteRoom.process(DeleteRoomRequest.builder()
                .roomId(roomId).build());
        return handleOutput(output, HttpStatus.OK);
    }

    @Operation(
            summary = "Register Room Guest Rest API",
            description = "Register Room Guest Rest API is registering guest to a room"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "HTTP STATUS 201 CREATED"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS 400 BAD REQUEST"),
            @ApiResponse(responseCode = "403", description = "HTTP STATUS 403 FORBIDDEN"),
    })
    @PostMapping(RestAPIRoutes.REGISTER_VISITOR)
    public ResponseEntity<?> register(
            @RequestBody RegisterGuestRequest request,
            @PathVariable String bookingId
    ) {
        Either<ErrorOutput, RegisterGuestResponse> output = registerGuest.process(RegisterGuestRequest
                .builder()
                .bookingId(bookingId)
                .guests(request.getGuests())
                .build());
        return handleOutput(output, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get Guest  report Rest API",
            description = "Get Guest report Rest API is for searching guest registrations"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "HTTP STATUS 200 SUCCESS"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS 400 BAD REQUEST"),
            @ApiResponse(responseCode = "403", description = "HTTP STATUS 403 FORBIDDEN"),
    })
    @GetMapping(RestAPIRoutes.GET_VISITORS_REPORT)
    public ResponseEntity<?> getGuestReport(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNo,
            @RequestParam(required = false) String idCardNo,
            @RequestParam(required = false) LocalDate idCardValidity,
            @RequestParam(required = false) String idCardAuthority,
            @RequestParam(required = false) LocalDate idCardIssueDate,
            @RequestParam(required = false) String roomNo

    ) {
        Either<ErrorOutput, GetGuestReportResponse> output = getGuestReport.process(GetGuestReportRequest.builder()
                .idCardIssueAuthority(idCardAuthority)
                .idCardIssueDate(idCardIssueDate)
                .idCardNo(idCardNo)
                .idCardValidity(idCardValidity)
                .roomNo(roomNo)
                .endDate(endDate)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNo(phoneNo)
                .startDate(startDate)
                .build());
        return handleOutput(output, HttpStatus.OK);
    }

}
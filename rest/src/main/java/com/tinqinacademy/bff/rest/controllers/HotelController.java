package com.tinqinacademy.bff.rest.controllers;

import com.tinqinacademy.hotel.api.RestAPIRoutes;
import com.tinqinacademy.hotel.api.enumerations.BathroomType;
import com.tinqinacademy.hotel.api.enumerations.BedSize;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoom;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoomInput;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoomOutput;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Tag(name = "Hotel REST APIs")
@RequiredArgsConstructor
public class HotelController extends BaseController {
    private final SearchRoom searchRoom;

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

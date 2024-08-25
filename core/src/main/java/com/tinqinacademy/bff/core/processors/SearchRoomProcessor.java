package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.searchroom.SearchRoom;
import com.tinqinacademy.bff.api.operations.searchroom.SearchRoomRequest;
import com.tinqinacademy.bff.api.operations.searchroom.SearchRoomResponse;
import com.tinqinacademy.hotel.api.enumerations.BathroomType;
import com.tinqinacademy.hotel.api.enumerations.BedSize;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoomOutput;
import com.tinqinacademy.hotel.restexport.HotelClient;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static io.vavr.API.Match;

@Service
@Slf4j
public class SearchRoomProcessor extends BaseProcessor implements SearchRoom {
    private final HotelClient hotelClient;
    public SearchRoomProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
    }

    @Override
    public Either<ErrorOutput, SearchRoomResponse> process(SearchRoomRequest request) {
        log.info("Start searchRoom {}", request);
        return Try.of(() -> {
            SearchRoomOutput output = hotelClient.searchRooms(
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getBedCount(),
                    request.getBedSize() == null ? null : request.getBedSize().toString(),
                    request.getBathroomType() == null ? null :request.getBathroomType().toString()
            );
            SearchRoomResponse response = SearchRoomResponse.builder()
                    .roomIds(output.getRoomIds())
                    .build();
            log.info("End searchRoom {}", response);
            return response;
        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

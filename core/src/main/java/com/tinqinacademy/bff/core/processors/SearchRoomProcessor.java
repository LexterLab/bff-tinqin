package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoom;
import com.tinqinacademy.hotel.api.operations.searchroom.SearchRoomInput;
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
    public Either<ErrorOutput, SearchRoomOutput> process(SearchRoomInput input) {
        log.info("Start searchRoom {}", input);
        return Try.of(() -> {
            SearchRoomOutput output = hotelClient.searchAvailableRooms(input.getStartDate(), input.getEndDate(),
                    input.getBedCount(), input.getBedSize(), input.getBathroomType());
            log.info("End searchRoom {}", output);
            return output;
        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

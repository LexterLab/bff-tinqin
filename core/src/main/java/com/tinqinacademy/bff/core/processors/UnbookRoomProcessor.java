package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.unbookroom.UnbookRoom;
import com.tinqinacademy.bff.api.operations.unbookroom.UnbookRoomRequest;
import com.tinqinacademy.bff.api.operations.unbookroom.UnbookRoomResponse;
import com.tinqinacademy.hotel.api.operations.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.unbookroom.UnbookRoomOutput;
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
public class UnbookRoomProcessor extends BaseProcessor implements UnbookRoom {
    private final HotelClient hotelClient;
    public UnbookRoomProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
    }

    @Override
    public Either<ErrorOutput, UnbookRoomResponse> process(UnbookRoomRequest request) {
        log.info("Start unbookRoom {}", request);
        return Try.of(() -> {
            validateInput(request);
            UnbookRoomInput input = UnbookRoomInput.builder()
                    .roomId(request.getRoomId())
                    .userId(request.getUserId())
                    .build();
            UnbookRoomOutput output = hotelClient.unbookRoom(request.getRoomId(), input);
            UnbookRoomResponse response = UnbookRoomResponse.builder().build();
            log.info("End unbookRoom {}", output);
            return response;
        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

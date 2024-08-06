package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.getroom.GetRoom;
import com.tinqinacademy.bff.api.operations.getroom.GetRoomRequest;
import com.tinqinacademy.bff.api.operations.getroom.GetRoomResponse;
import com.tinqinacademy.hotel.api.operations.getroom.GetRoomOutput;
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
public class GetRoomProcessor extends BaseProcessor implements GetRoom {
    private final HotelClient hotelClient;
    public GetRoomProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
    }

    @Override
    public Either<ErrorOutput, GetRoomResponse> process(GetRoomRequest request) {
        log.info("Start getRoom  {}", request);
        return Try.of(() -> {
            validateInput(request);
            GetRoomOutput output = hotelClient.getRoomById(request.getRoomId());
            GetRoomResponse response = conversionService.convert(output, GetRoomResponse.class);
            log.info("End getRoom  {}", response);
            return response;
        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

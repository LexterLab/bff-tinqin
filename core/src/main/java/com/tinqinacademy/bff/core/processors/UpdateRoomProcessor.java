package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.errors.ErrorOutput;

import com.tinqinacademy.bff.api.operations.updateroom.UpdateRoom;
import com.tinqinacademy.bff.api.operations.updateroom.UpdateRoomRequest;
import com.tinqinacademy.bff.api.operations.updateroom.UpdateRoomResponse;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomOutput;
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
public class UpdateRoomProcessor extends BaseProcessor implements UpdateRoom {
    private final HotelClient hotelClient;

    public UpdateRoomProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
    }

    @Override
    public Either<ErrorOutput, UpdateRoomResponse> process(UpdateRoomRequest request) {
        log.info("Start updateRoom {}", request);
       return Try.of(() -> {
            validateInput(request);
            UpdateRoomInput input = conversionService.convert(request, UpdateRoomInput.class);
            UpdateRoomOutput output = hotelClient.updateRoom(input.getRoomId(), input);
            UpdateRoomResponse response =  UpdateRoomResponse.builder()
                    .roomId(output.getRoomId())
                    .build();
            log.info("End updateRoom {}", response);
            return response;
        }).toEither()
               .mapLeft(throwable -> Match(throwable).of(
                       validatorCase(throwable),
                       feignCase(throwable),
                       defaultCase(throwable)
               ));
    }
}

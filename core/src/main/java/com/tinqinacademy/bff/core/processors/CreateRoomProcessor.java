package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.createroom.CreateRoom;
import com.tinqinacademy.bff.api.createroom.CreateRoomInput;
import com.tinqinacademy.bff.api.createroom.CreateRoomOutput;
import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.restexport.HotelClient;
import feign.FeignException;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.vavr.API.Match;

@Service
@Slf4j
public class CreateRoomProcessor extends BaseProcessor  implements CreateRoom {
    private final HotelClient client;

    public CreateRoomProcessor(ConversionService conversionService, Validator validator, HotelClient client) {
        super(conversionService, validator);
        this.client = client;
    }

    @Override
    public Either<ErrorOutput, CreateRoomOutput> process(CreateRoomInput input) {
        log.info("Start createRoom {}", input);
        return Try.of(() -> {
            validateInput(input);
            com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput convertedInput = conversionService
                    .convert(input, com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput.class);
            com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOutput output = client
                    .createRoom(convertedInput);

            CreateRoomOutput convertedOutput = CreateRoomOutput
                    .builder()
                    .roomId(output.getRoomId())
                    .build();


            log.info("End createRoom {}", output);
            return convertedOutput;
        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
//                        customCase(throwable, HttpStatus.BAD_REQUEST, FeignException.class),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));

    }
}

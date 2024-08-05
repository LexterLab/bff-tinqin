package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.createroom.CreateRoom;
import com.tinqinacademy.bff.api.createroom.CreateRoomOpInput;
import com.tinqinacademy.bff.api.createroom.CreateRoomOpOutput;
import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOutput;
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
public class CreateRoomProcessor extends BaseProcessor  implements CreateRoom {
    private final HotelClient client;

    public CreateRoomProcessor(ConversionService conversionService, Validator validator, HotelClient client) {
        super(conversionService, validator);
        this.client = client;
    }

    @Override
    public Either<ErrorOutput, CreateRoomOpOutput> process(CreateRoomOpInput input) {
        log.info("Start createRoom {}", input);
        return Try.of(() -> {
            validateInput(input);
            CreateRoomInput convertedInput = conversionService
                    .convert(input, com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput.class);
            CreateRoomOutput output = client
                    .createRoom(convertedInput);

            CreateRoomOpOutput convertedOutput = CreateRoomOpOutput
                    .builder()
                    .roomId(output.getRoomId())
                    .build();

            log.info("End createRoom {}", output);
            return convertedOutput;
        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));

    }
}

package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoom;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.restexport.HotelClient;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static io.vavr.API.Match;

@Slf4j
@Service
public class DeleteRoomProcessor extends BaseProcessor implements DeleteRoom {
    private final HotelClient hotelClient;
    public DeleteRoomProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
    }

    @Override
    public Either<ErrorOutput, DeleteRoomOutput> process(DeleteRoomInput input) {
        log.info("Start deleteRoom {}", input);
        return Try.of(() -> {
            validateInput(input);
            DeleteRoomOutput output = hotelClient.deleteRoom(input.getRoomId());
            log.info("End deleteRoom {}", output);
            return output;
        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

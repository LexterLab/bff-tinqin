package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.partialupdateroom.PartialUpdateRoom;
import com.tinqinacademy.bff.api.operations.partialupdateroom.PartialUpdateRoomRequest;
import com.tinqinacademy.bff.api.operations.partialupdateroom.PartialUpdateRoomResponse;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.PartialUpdateRoomOutput;
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
public class PartialUpdateRoomProcessor extends BaseProcessor implements PartialUpdateRoom {
    private final HotelClient hotelClient;
    public PartialUpdateRoomProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
    }

    @Override
    public Either<ErrorOutput, PartialUpdateRoomResponse> process(PartialUpdateRoomRequest request) {
        log.info("Start partialUpdateRoom {}", request);
        return Try.of(() -> {
            validateInput(request);
            PartialUpdateRoomInput input = conversionService.convert(request, PartialUpdateRoomInput.class);
            PartialUpdateRoomOutput output = hotelClient.partialUpdateRoom(request.getRoomId(), input);
            PartialUpdateRoomResponse response = PartialUpdateRoomResponse.builder()
                    .roomId(output.getRoomId())
                    .build();
            log.info("End partialUpdateRoom {}", response);
            return response;
        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

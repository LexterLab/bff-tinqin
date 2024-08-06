package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.registerguest.RegisterGuest;
import com.tinqinacademy.bff.api.operations.registerguest.RegisterGuestRequest;
import com.tinqinacademy.bff.api.operations.registerguest.RegisterGuestResponse;
import com.tinqinacademy.hotel.api.operations.registerguest.RegisterGuestInput;
import com.tinqinacademy.hotel.api.operations.registerguest.RegisterGuestOutput;
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
public class RegisterGuestProcessor extends BaseProcessor implements RegisterGuest {
    private final HotelClient hotelClient;
    public RegisterGuestProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
    }

    @Override
    public Either<ErrorOutput, RegisterGuestResponse> process(RegisterGuestRequest request) {
        log.info("Start registerGuest {}", request);
        return Try.of(() -> {
            validateInput(request);
            RegisterGuestInput input = conversionService.convert(request, RegisterGuestInput.class);
            RegisterGuestOutput output = hotelClient.registerGuest(input, request.getBookingId());
            RegisterGuestResponse response = RegisterGuestResponse.builder().build();
            log.info("End registerGuest {}", output);
            return response;
        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

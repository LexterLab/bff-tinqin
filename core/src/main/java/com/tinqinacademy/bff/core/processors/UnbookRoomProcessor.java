package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.authentication.api.operations.getuser.GetUserOutput;
import com.tinqinacademy.authentication.restexport.AuthenticationClient;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static io.vavr.API.Match;

@Service
@Slf4j
public class UnbookRoomProcessor extends BaseProcessor implements UnbookRoom {
    private final HotelClient hotelClient;
    private final AuthenticationClient authenticationClient;
    public UnbookRoomProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient,
                               AuthenticationClient authenticationClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
        this.authenticationClient = authenticationClient;
    }

    @Override
    public Either<ErrorOutput, UnbookRoomResponse> process(UnbookRoomRequest request) {
        log.info("Start unbookRoom {}", request);
        return Try.of(() -> {
            validateInput(request);

            GetUserOutput userOutput = authenticationClient.getUserInfo(getAuthenticatedUser());

            UnbookRoomInput input = UnbookRoomInput.builder()
                    .bookingId(request.getBookingId())
                    .userId(userOutput.getId().toString())
                    .build();

            UnbookRoomOutput output = hotelClient.unbookRoom(request.getBookingId(), input);
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

    private String getAuthenticatedUser() {
        log.info("Start getAuthenticatedUser");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("End getAuthenticatedUser {}", username);
        return username;
    }
}

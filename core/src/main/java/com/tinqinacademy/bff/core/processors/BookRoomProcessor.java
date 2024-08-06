package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.bookroom.BookRoom;
import com.tinqinacademy.bff.api.operations.bookroom.BookRoomRequest;
import com.tinqinacademy.bff.api.operations.bookroom.BookRoomResponse;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomOutput;
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
public class BookRoomProcessor extends BaseProcessor implements BookRoom {
    private final HotelClient hotelClient;

    public BookRoomProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
    }

    @Override
    public Either<ErrorOutput, BookRoomResponse> process(BookRoomRequest request) {
     log.info("Start bookRoom {}", request);
      return Try.of(() -> {
          validateInput(request);
          BookRoomInput input = conversionService.convert(request, BookRoomInput.class);
          BookRoomOutput output = hotelClient.bookRoom(input.getRoomId(), input);
          BookRoomResponse response = BookRoomResponse.builder().build();
          log.info("End bookRoom {}", response);
          return response;
      }).toEither()
              .mapLeft(throwable -> Match(throwable).of(
                      validatorCase(throwable),
                      feignCase(throwable),
                      defaultCase(throwable)
              ));
    }
}

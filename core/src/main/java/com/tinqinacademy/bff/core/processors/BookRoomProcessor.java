package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoom;
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
    public Either<ErrorOutput, BookRoomOutput> process(BookRoomInput input) {
     log.info("Start bookRoom {}", input);
      return Try.of(() -> {
          validateInput(input);
          BookRoomOutput output = hotelClient.bookRoom(input.getRoomId(), input);
          log.info("End bookRoom {}", output);
          return output;
      }).toEither()
              .mapLeft(throwable -> Match(throwable).of(
                      validatorCase(throwable),
                      feignCase(throwable),
                      defaultCase(throwable)
              ));
    }
}

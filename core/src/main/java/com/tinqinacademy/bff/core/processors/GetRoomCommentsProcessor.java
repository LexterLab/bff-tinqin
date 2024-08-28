package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.getroomcomments.GetRoomComments;
import com.tinqinacademy.bff.api.operations.getroomcomments.GetRoomCommentsRequest;
import com.tinqinacademy.bff.api.operations.getroomcomments.GetRoomCommentsResponse;
import com.tinqinacademy.comments.api.operations.getroomcomments.GetRoomCommentsOutput;
import com.tinqinacademy.comments.restexport.CommentClient;
import com.tinqinacademy.hotel.api.operations.getroom.GetRoomOutput;
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
public class GetRoomCommentsProcessor extends BaseProcessor  implements GetRoomComments {
    private final HotelClient hotelClient;
    private final CommentClient commentClient;

    public GetRoomCommentsProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient,
                                    CommentClient commentClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
        this.commentClient = commentClient;
    }

    @Override
    public Either<ErrorOutput, GetRoomCommentsResponse> process(GetRoomCommentsRequest request) {
        log.info("Start getRoomComments {}", request);
        return  Try.of(() -> {
            validateInput(request);
            GetRoomOutput roomOutput = hotelClient.getRoomById(request.getRoomId());
            GetRoomCommentsOutput commentsOutput = commentClient.getRoomComments(roomOutput.getId().toString());
            GetRoomCommentsResponse response = conversionService.convert(commentsOutput, GetRoomCommentsResponse.class);
            log.info("End getRoomComments {}", response);
            return response;
        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.leaveroomcomment.LeaveRoomComment;
import com.tinqinacademy.bff.api.operations.leaveroomcomment.LeaveRoomCommentRequest;
import com.tinqinacademy.bff.api.operations.leaveroomcomment.LeaveRoomCommentResponse;
import com.tinqinacademy.comments.api.operations.leaveroomcomment.LeaveRoomCommentInput;
import com.tinqinacademy.comments.api.operations.leaveroomcomment.LeaveRoomCommentOutput;
import com.tinqinacademy.comments.restexport.restexport.CommentClient;
import com.tinqinacademy.hotel.api.operations.getroom.GetRoomOutput;
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
public class LeaveRoomCommentProcessor extends BaseProcessor implements LeaveRoomComment {
    private final HotelClient hotelClient;
    private final CommentClient commentClient;
    public LeaveRoomCommentProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient,
                                     CommentClient commentClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
        this.commentClient = commentClient;
    }

    @Override
    public Either<ErrorOutput, LeaveRoomCommentResponse> process(LeaveRoomCommentRequest request) {
        log.info("Start leaveRoomComment {}", request);
        return Try.of(() -> {
            GetRoomOutput roomOutput = hotelClient.getRoomById(request.getRoomId());
            LeaveRoomCommentInput input = conversionService.convert(request, LeaveRoomCommentInput.class);
            LeaveRoomCommentOutput output = commentClient.leaveRoomComment(String.valueOf(roomOutput.getId()), input);
            LeaveRoomCommentResponse response = LeaveRoomCommentResponse
                    .builder()
                    .id(output.getId())
                    .build();
            log.info("End leaveRoomComment {}", response);
            return response;
        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

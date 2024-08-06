//package com.tinqinacademy.bff.core.processors;
//
//import com.tinqinacademy.comments.api.error.ErrorOutput;
//import com.tinqinacademy.comments.api.operations.leaveroomcomment.LeaveRoomComment;
//import com.tinqinacademy.comments.api.operations.leaveroomcomment.LeaveRoomCommentInput;
//import com.tinqinacademy.comments.api.operations.leaveroomcomment.LeaveRoomCommentOutput;
//import com.tinqinacademy.comments.restexport.restexport.CommentClient;
//import com.tinqinacademy.hotel.api.operations.getroom.GetRoomOutput;
//import com.tinqinacademy.hotel.restexport.HotelClient;
//import io.vavr.control.Either;
//import io.vavr.control.Try;
//import jakarta.validation.Validator;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.convert.ConversionService;
//import org.springframework.stereotype.Service;
//
//import static io.vavr.API.Match;
//
//@Service
//@Slf4j
//public class LeaveRoomCommentProcessor extends BaseProcessor implements LeaveRoomComment {
//    private final HotelClient hotelClient;
//    private final CommentClient commentClient;
//    public LeaveRoomCommentProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient,
//                                     CommentClient commentClient) {
//        super(conversionService, validator);
//        this.hotelClient = hotelClient;
//        this.commentClient = commentClient;
//    }
//
//    @Override
//    public Either<ErrorOutput, LeaveRoomCommentOutput> process(LeaveRoomCommentInput input) {
//        log.info("Start leaveRoomComment {}", input);
//        return Try.of(() -> {
//            GetRoomOutput roomOutput = hotelClient.getRoomById(input.getRoomId());
//            LeaveRoomCommentOutput output = commentClient.leaveRoomComment(String.valueOf(roomOutput.getId()), input);
//            log.info("End leaveRoomComment {}", output);
//            return output;
//        }).toEither()
//                .mapLeft(throwable -> Match(throwable).of(
//                        validatorCase(throwable),
//                        feignCase(throwable),
//                        defaultCase(throwable)
//                ));
//
//
//    }
//}

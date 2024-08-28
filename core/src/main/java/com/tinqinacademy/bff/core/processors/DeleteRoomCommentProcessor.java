package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.deleteroomcomment.DeleteRoomComment;
import com.tinqinacademy.bff.api.operations.deleteroomcomment.DeleteRoomCommentRequest;
import com.tinqinacademy.bff.api.operations.deleteroomcomment.DeleteRoomCommentResponse;
import com.tinqinacademy.comments.restexport.restexport.CommentClient;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static io.vavr.API.Match;

@Service
@Slf4j
public class DeleteRoomCommentProcessor extends BaseProcessor implements DeleteRoomComment {
    private final CommentClient commentClient;

    public DeleteRoomCommentProcessor(ConversionService conversionService, Validator validator,
                                      CommentClient commentClient) {
        super(conversionService, validator);
        this.commentClient = commentClient;
    }

    @Override
    public Either<ErrorOutput, DeleteRoomCommentResponse> process(DeleteRoomCommentRequest input) {
        log.info("Start deleteRoomComment {}", input);
        return Try.of(() -> {
            validateInput(input);

            commentClient.deleteRoomComment(input.getCommentId());

            DeleteRoomCommentResponse response = DeleteRoomCommentResponse.builder().build();

            log.info("End deleteRoomComment {}", response);

            return response;

        }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

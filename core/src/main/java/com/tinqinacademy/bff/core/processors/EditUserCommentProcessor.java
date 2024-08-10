package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.authentication.api.operations.getuser.GetUserOutput;
import com.tinqinacademy.authentication.restexport.AuthenticationClient;
import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.editusercomment.EditUserComment;
import com.tinqinacademy.bff.api.operations.editusercomment.EditUserCommentRequest;
import com.tinqinacademy.bff.api.operations.editusercomment.EditUserCommentResponse;
import com.tinqinacademy.comments.api.operations.editusercomment.EditUserCommentInput;
import com.tinqinacademy.comments.api.operations.editusercomment.EditUserCommentOutput;
import com.tinqinacademy.comments.restexport.restexport.CommentClient;
import com.tinqinacademy.hotel.api.operations.findroombyroomno.FindRoomByRoomNoOutput;
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
public class EditUserCommentProcessor extends BaseProcessor implements EditUserComment {
    private final AuthenticationClient authenticationClient;
    private final HotelClient hotelClient;
    private final CommentClient commentClient;

    public EditUserCommentProcessor(ConversionService conversionService, Validator validator,
                                    AuthenticationClient authenticationClient, HotelClient hotelClient,
                                    CommentClient commentClient) {
        super(conversionService, validator);
        this.authenticationClient = authenticationClient;
        this.hotelClient = hotelClient;
        this.commentClient = commentClient;
    }

    @Override
    public Either<ErrorOutput, EditUserCommentResponse> process(EditUserCommentRequest input) {
        log.info("Start editUserComment {}", input);
        return Try.of(() -> {
            validateInput(input);

            String username = getAuthenticatedUser();
            GetUserOutput getUserOutput = authenticationClient.getUser(username);

            FindRoomByRoomNoOutput getRoomOutput = hotelClient.findRoomByRoomNo(input.getRoomNo());

            EditUserCommentInput commentInput = conversionService.convert(input, EditUserCommentInput.class);
            commentInput.setUserId(getUserOutput.getId().toString());
            commentInput.setRoomId(getRoomOutput.getId().toString());

            EditUserCommentOutput editUserCommentOutput = commentClient.editUserComment(input.getCommentId(), commentInput);

            EditUserCommentResponse response = EditUserCommentResponse
                    .builder()
                    .id(editUserCommentOutput.getId())
                    .build();

            log.info("End editUserComment {}", response);

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

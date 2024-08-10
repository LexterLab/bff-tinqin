package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.authentication.api.operations.getuser.GetUserInput;
import com.tinqinacademy.authentication.api.operations.getuser.GetUserOutput;
import com.tinqinacademy.authentication.restexport.AuthenticationClient;
import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.editcomment.EditComment;
import com.tinqinacademy.bff.api.operations.editcomment.EditCommentRequest;
import com.tinqinacademy.bff.api.operations.editcomment.EditCommentResponse;
import com.tinqinacademy.comments.api.operations.editcomment.EditCommentInput;
import com.tinqinacademy.comments.api.operations.editcomment.EditCommentOutput;
import com.tinqinacademy.comments.restexport.restexport.CommentClient;
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
public class EditCommentProcessor extends BaseProcessor implements EditComment {
    private final CommentClient commentClient;
    private final AuthenticationClient authenticationClient;

    public EditCommentProcessor(ConversionService conversionService, Validator validator, CommentClient commentClient,
                                AuthenticationClient authenticationClient) {
        super(conversionService, validator);
        this.commentClient = commentClient;
        this.authenticationClient = authenticationClient;
    }

    @Override
    public Either<ErrorOutput, EditCommentResponse> process(EditCommentRequest input) {
        log.info("Start editComment {}", input);

        return Try.of(() -> {
            validateInput(input);

            String username = getAuthenticatedUser();

            GetUserOutput getUserOutput = authenticationClient.getUser(username);

            EditCommentInput editCommentInput = buildEditCommentInput(input, getUserOutput);

            EditCommentOutput editCommentOutput = commentClient.editComment(input.getCommentId(), editCommentInput);

            EditCommentResponse response = EditCommentResponse
                    .builder()
                    .id(editCommentOutput.getId())
                    .build();

            log.info("End editComment {}", response);
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

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "domino222";

        log.info("End getAuthenticatedUser {}", username);
        return username;
    }

    private EditCommentInput buildEditCommentInput(EditCommentRequest input, GetUserOutput getUserOutput) {
        log.info("Start buildEditCommentInput {}", input);
        EditCommentInput editCommentInput = EditCommentInput.builder()
                .id(input.getCommentId())
                .userId(getUserOutput.getId().toString())
                .content(input.getContent())
                .build();

        log.info("End buildEditCommentInput {}", editCommentInput);
        return editCommentInput;
    }

}

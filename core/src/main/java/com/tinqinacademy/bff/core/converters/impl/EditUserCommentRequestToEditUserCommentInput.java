package com.tinqinacademy.bff.core.converters.impl;

import com.tinqinacademy.bff.api.operations.editusercomment.EditUserCommentRequest;
import com.tinqinacademy.bff.core.converters.AbstractConverter;
import com.tinqinacademy.comments.api.operations.editusercomment.EditUserCommentInput;
import org.springframework.stereotype.Component;

@Component
public class EditUserCommentRequestToEditUserCommentInput extends AbstractConverter<EditUserCommentRequest, EditUserCommentInput> {
    @Override
    protected Class<EditUserCommentInput> getTargetClass() {
        return EditUserCommentInput.class;
    }

    @Override
    protected EditUserCommentInput doConvert(EditUserCommentRequest source) {
        EditUserCommentInput target = EditUserCommentInput.builder()
                .commentId(source.getCommentId())
                .content(source.getContent())
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .build();

        return target;
    }
}

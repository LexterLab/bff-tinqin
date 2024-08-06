package com.tinqinacademy.bff.core.converters.impl;

import com.tinqinacademy.bff.api.operations.leaveroomcomment.LeaveRoomCommentRequest;
import com.tinqinacademy.bff.core.converters.AbstractConverter;
import com.tinqinacademy.comments.api.operations.leaveroomcomment.LeaveRoomCommentInput;
import org.springframework.stereotype.Component;

@Component
public class LeaveRoomCommentRequestToLeaveRoomCommentInput extends AbstractConverter<LeaveRoomCommentRequest, LeaveRoomCommentInput> {
    @Override
    protected Class<LeaveRoomCommentInput> getTargetClass() {
        return LeaveRoomCommentInput.class;
    }

    @Override
    protected LeaveRoomCommentInput doConvert(LeaveRoomCommentRequest source) {
        LeaveRoomCommentInput target = LeaveRoomCommentInput.builder()
                .lastName(source.getLastName())
                .firstName(source.getFirstName())
                .content(source.getContent())
                .roomId(source.getRoomId())
                .build();
        return target;
    }
}

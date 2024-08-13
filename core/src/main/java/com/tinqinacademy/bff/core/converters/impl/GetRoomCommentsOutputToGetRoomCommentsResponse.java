package com.tinqinacademy.bff.core.converters.impl;

import com.tinqinacademy.bff.api.operations.getroomcomments.GetRoomCommentsResponse;
import com.tinqinacademy.bff.api.operations.getroomcomments.RoomCommentData;
import com.tinqinacademy.bff.core.converters.AbstractConverter;
import com.tinqinacademy.comments.api.operations.getroomcomments.GetRoomCommentsOutput;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetRoomCommentsOutputToGetRoomCommentsResponse extends AbstractConverter<GetRoomCommentsOutput, GetRoomCommentsResponse> {

    @Override
    protected Class<GetRoomCommentsResponse> getTargetClass() {
        return GetRoomCommentsResponse.class;
    }

    @Override
    protected GetRoomCommentsResponse doConvert(GetRoomCommentsOutput source) {
        GetRoomCommentsResponse response = GetRoomCommentsResponse
                .builder()
                .roomComments(convertComments(source))
                .build();
        return response;
    }

    private List<RoomCommentData> convertComments(GetRoomCommentsOutput source) {
        List<RoomCommentData> comments = new ArrayList<>();
        source.getRoomComments()
                .forEach(comment -> comments.add(RoomCommentData
                        .builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .firstName(comment.getFirstName())
                                .lastName(comment.getLastName())
                                .lastEditedBy(comment.getLastEditedBy())
                                .publishDate(comment.getPublishDate())
                                .lastEditedDate(comment.getLastEditedDate())
                        .build()));
        return comments;
    }
}

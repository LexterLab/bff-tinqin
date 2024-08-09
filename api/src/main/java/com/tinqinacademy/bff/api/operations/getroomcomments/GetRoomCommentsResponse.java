package com.tinqinacademy.bff.api.operations.getroomcomments;

import com.tinqinacademy.bff.api.base.OperationOutput;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GetRoomCommentsResponse implements OperationOutput {
    List<RoomCommentData> roomComments;

}

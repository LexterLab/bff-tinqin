package com.tinqinacademy.bff.api.operations.leaveroomcomment;

import com.tinqinacademy.bff.api.base.OperationOutput;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class LeaveRoomCommentResponse implements OperationOutput {
    private UUID id;
}

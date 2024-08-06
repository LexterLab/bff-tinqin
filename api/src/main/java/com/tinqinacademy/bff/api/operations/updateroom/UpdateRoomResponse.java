package com.tinqinacademy.bff.api.operations.updateroom;

import com.tinqinacademy.bff.api.base.OperationOutput;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class UpdateRoomResponse implements OperationOutput {
    private UUID roomId;
}

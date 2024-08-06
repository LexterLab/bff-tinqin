package com.tinqinacademy.bff.api.operations.createroom;

import com.tinqinacademy.bff.api.base.OperationOutput;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CreateRoomResponse implements OperationOutput {
    String roomId;
}

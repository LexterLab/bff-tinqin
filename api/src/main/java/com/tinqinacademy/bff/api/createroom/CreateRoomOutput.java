package com.tinqinacademy.bff.api.createroom;

import com.tinqinacademy.bff.api.base.OperationOutput;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CreateRoomOutput implements OperationOutput {
    String roomId;
}

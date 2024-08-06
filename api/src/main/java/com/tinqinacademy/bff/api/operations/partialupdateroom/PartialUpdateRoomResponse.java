package com.tinqinacademy.bff.api.operations.partialupdateroom;

import com.tinqinacademy.bff.api.base.OperationOutput;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class PartialUpdateRoomResponse implements OperationOutput {
    @Schema(example = "UUID")
    private UUID roomId;
}

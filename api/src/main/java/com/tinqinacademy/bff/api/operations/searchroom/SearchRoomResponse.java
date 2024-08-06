package com.tinqinacademy.bff.api.operations.searchroom;

import com.tinqinacademy.bff.api.base.OperationOutput;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class SearchRoomResponse implements OperationOutput {
    private List<UUID> roomIds;
}

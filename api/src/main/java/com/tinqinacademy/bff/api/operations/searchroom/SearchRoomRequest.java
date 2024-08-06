package com.tinqinacademy.bff.api.operations.searchroom;

import com.tinqinacademy.bff.api.base.OperationInput;
import com.tinqinacademy.bff.api.enumerations.BathroomType;
import com.tinqinacademy.bff.api.enumerations.BedSize;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class SearchRoomRequest implements OperationInput {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer bedCount;
    private BedSize bedSize;
    private BathroomType bathroomType;
}

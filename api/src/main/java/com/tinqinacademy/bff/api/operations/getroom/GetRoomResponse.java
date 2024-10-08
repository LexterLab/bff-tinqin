package com.tinqinacademy.bff.api.operations.getroom;

import com.tinqinacademy.bff.api.base.OperationOutput;
import com.tinqinacademy.bff.api.enumerations.BathroomType;
import com.tinqinacademy.bff.api.enumerations.BedSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class GetRoomResponse implements OperationOutput {
    private UUID id;
    @Schema(example = "3232")
    private BigDecimal price;
    @Schema(example = "4")
    private Integer floor;
    private List<BedSize> bedSizes;
    private BathroomType bathroomType;
    private List<LocalDateTime> datesOccupied;
    private Integer bedCount;
}

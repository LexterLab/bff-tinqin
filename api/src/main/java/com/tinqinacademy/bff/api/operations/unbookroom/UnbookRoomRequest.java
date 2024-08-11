package com.tinqinacademy.bff.api.operations.unbookroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.bff.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class UnbookRoomRequest implements OperationInput {
    @JsonIgnore
    @UUID(message = "Field roomId must be UUID")
    @NotBlank(message = "Field roomId must not be blank")
    private String bookingId;
}

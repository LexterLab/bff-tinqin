package com.tinqinacademy.bff.api.operations.getroom;

import com.tinqinacademy.bff.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class GetRoomRequest implements OperationInput {
    @UUID(message = "Field roomId must be UUID")
    @NotBlank(message = "Field roomId must not be blank")
    private String roomId;
}

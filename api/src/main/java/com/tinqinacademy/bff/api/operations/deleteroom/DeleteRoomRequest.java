package com.tinqinacademy.bff.api.operations.deleteroom;

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
public class DeleteRoomRequest implements OperationInput {
    @NotBlank(message = "Field roomId must not be blank")
    @UUID(message = "Field roomId must be UUID")
    private String roomId;
}

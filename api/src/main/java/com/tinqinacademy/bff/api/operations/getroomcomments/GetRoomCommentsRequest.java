package com.tinqinacademy.bff.api.operations.getroomcomments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.bff.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GetRoomCommentsRequest implements OperationInput {
    @UUID(message = "Field roomId must be UUID")
    @NotBlank(message = "Field roomId must not be blank")
    @JsonIgnore
    private String roomId;
}

package com.tinqinacademy.bff.api.operations.deleteroomcomment;

import com.tinqinacademy.bff.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class DeleteRoomCommentRequest implements OperationInput {
    @NotBlank(message = "Field commentId must not be blank")
    @UUID(message = "Field commentId must be UUID")
    private String commentId;
}

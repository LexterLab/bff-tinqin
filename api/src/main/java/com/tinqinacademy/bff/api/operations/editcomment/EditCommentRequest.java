package com.tinqinacademy.bff.api.operations.editcomment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.bff.api.base.OperationInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class EditCommentRequest implements OperationInput {
    @JsonIgnore
    @UUID(message = "Field id must be UUID")
    @NotBlank(message = "Field id must not be blank")
    private String commentId;
    @Schema(example = "This room is not as sick as i thought BRUV!!!")
    @NotBlank(message = "Field content cannot be blank")
    @Size(max = 500, min = 5, message = "Field content must be 5-500 characters")
    private String content;
}

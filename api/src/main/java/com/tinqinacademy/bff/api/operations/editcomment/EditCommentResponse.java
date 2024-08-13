package com.tinqinacademy.bff.api.operations.editcomment;

import com.tinqinacademy.bff.api.base.OperationOutput;
import lombok.*;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class EditCommentResponse implements OperationOutput {
    private UUID id;
}

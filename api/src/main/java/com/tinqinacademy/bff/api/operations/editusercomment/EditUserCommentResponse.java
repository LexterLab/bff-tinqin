package com.tinqinacademy.bff.api.operations.editusercomment;

import com.tinqinacademy.bff.api.base.OperationOutput;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EditUserCommentResponse implements OperationOutput {
    private UUID id;
}

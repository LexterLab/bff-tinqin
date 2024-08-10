package com.tinqinacademy.bff.api.operations.getroomcomments;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RoomCommentData {
    private UUID id;
    @Schema(example = "George")
    private String firstName;
    @Schema(example = "Russell")
    private String lastName;
    @Schema(example = "Insane room 10/10")
    private String content;
    private LocalDateTime publishDate;
    private LocalDateTime lastEditedDate;
    @Schema(example = "George Russell")
    private UUID lastEditedBy;
}

package com.tinqinacademy.bff.api.errors;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Error {
    private String message;
    private String field;
}

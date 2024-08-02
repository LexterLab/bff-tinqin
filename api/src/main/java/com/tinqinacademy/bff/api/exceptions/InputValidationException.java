package com.tinqinacademy.bff.api.exceptions;

import com.tinqinacademy.bff.api.errors.Error;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class InputValidationException extends RuntimeException {
    private List<Error> errors;
}

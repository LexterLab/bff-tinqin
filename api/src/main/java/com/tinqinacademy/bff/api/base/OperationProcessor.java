package com.tinqinacademy.bff.api.base;

import com.tinqinacademy.bff.api.errors.ErrorOutput;
import io.vavr.control.Either;

public interface OperationProcessor <I extends OperationInput, O extends OperationOutput> {
    Either<ErrorOutput, O> process(I input);
}
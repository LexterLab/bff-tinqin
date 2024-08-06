package com.tinqinacademy.bff.api.operations.registerguest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.bff.api.base.OperationInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class RegisterGuestRequest implements OperationInput {
    @JsonIgnore
    @NotBlank(message = "Field bookingId must not be blank")
    @UUID(message = "Field bookingId must be UUID")
    private String bookingId;
    @NotNull(message = "Field guests cannot be null")
    private List<@Valid GuestInput> guests;
}

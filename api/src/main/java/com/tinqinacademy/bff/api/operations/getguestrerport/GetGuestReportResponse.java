package com.tinqinacademy.bff.api.operations.getguestrerport;

import com.tinqinacademy.bff.api.base.OperationOutput;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GetGuestReportResponse implements OperationOutput {
    List<GuestOutput> guests;
}

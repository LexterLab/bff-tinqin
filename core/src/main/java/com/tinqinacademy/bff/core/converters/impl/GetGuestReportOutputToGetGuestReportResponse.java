package com.tinqinacademy.bff.core.converters.impl;

import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReportRequest;
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReportResponse;
import com.tinqinacademy.bff.api.operations.getguestrerport.GuestOutput;
import com.tinqinacademy.bff.core.converters.AbstractConverter;
import com.tinqinacademy.hotel.api.operations.getguestreport.GetGuestReportOutput;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetGuestReportOutputToGetGuestReportResponse extends AbstractConverter<GetGuestReportOutput, GetGuestReportResponse> {

    @Override
    protected Class<GetGuestReportResponse> getTargetClass() {
        return GetGuestReportResponse.class;
    }

    @Override
    protected GetGuestReportResponse doConvert(GetGuestReportOutput source) {
        GetGuestReportResponse response = GetGuestReportResponse
                .builder()
                .guests(convertGuests(source))
                .build();
        return response;
    }

    private List<GuestOutput> convertGuests(GetGuestReportOutput source) {
        List<GuestOutput> guests = new ArrayList<>();
        source.getGuestsReports()
               .forEach(guestOutput -> guests.add(GuestOutput.builder()
                               .startDate(guestOutput.getStartDate())
                               .endDate(guestOutput.getEndDate())
                               .lastName(guestOutput.getLastName())
                               .firstName(guestOutput.getFirstName())
                               .idCardNo(guestOutput.getIdCardNo())
                               .idCardValidity(guestOutput.getIdCardValidity())
                               .idCardIssueAuthority(guestOutput.getIdCardIssueAuthority())
                               .idCardIssueDate(guestOutput.getIdCardIssueDate())
                       .build()));
        return guests;
    }

}

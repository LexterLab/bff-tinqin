package com.tinqinacademy.bff.core.converters.impl;

import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReportRequest;
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReportResponse;
import com.tinqinacademy.bff.core.converters.AbstractConverter;
import com.tinqinacademy.hotel.api.operations.getguestreport.GetGuestReportOutput;
import org.springframework.stereotype.Component;

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
                .guests()
                .build();
        return null;
    }

}

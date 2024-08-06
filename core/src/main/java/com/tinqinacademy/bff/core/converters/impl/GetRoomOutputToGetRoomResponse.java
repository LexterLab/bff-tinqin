package com.tinqinacademy.bff.core.converters.impl;

import com.tinqinacademy.bff.api.enumerations.BathroomType;
import com.tinqinacademy.bff.api.enumerations.BedSize;
import com.tinqinacademy.bff.api.operations.getroom.GetRoomResponse;
import com.tinqinacademy.bff.core.converters.AbstractConverter;
import com.tinqinacademy.hotel.api.operations.getroom.GetRoomOutput;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetRoomOutputToGetRoomResponse extends AbstractConverter<GetRoomOutput, GetRoomResponse> {
    @Override
    protected Class<GetRoomResponse> getTargetClass() {
        return GetRoomResponse.class;
    }

    @Override
    protected GetRoomResponse doConvert(GetRoomOutput source) {
        GetRoomResponse response = GetRoomResponse
                .builder()
                .id(source.getId())
                .price(source.getPrice())
                .floor(source.getFloor())
                .bedSizes(convertBeds(source))
                .datesOccupied(source.getDatesOccupied())
                .bedCount(source.getBedCount())
                .bathroomType(BathroomType.getByCode(source.getBathroomType().toString()))
                .build();
        return response;
    }

    private List<com.tinqinacademy.bff.api.enumerations.BedSize> convertBeds(GetRoomOutput source) {
        List<com.tinqinacademy.bff.api.enumerations.BedSize> bedSizes = source.getBedSizes().stream()
                .map(bedSize -> BedSize.getByCode(bedSize.toString()))
                .toList();
        return bedSizes;
    }
}

package com.tinqinacademy.bff.core.converters.impl;

import com.tinqinacademy.bff.api.operations.updateroom.UpdateRoomRequest;
import com.tinqinacademy.bff.core.converters.AbstractConverter;
import com.tinqinacademy.hotel.api.enumerations.BathroomType;
import com.tinqinacademy.hotel.api.enumerations.BedSize;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdateRoomRequestToUpdateRoomInput extends AbstractConverter<UpdateRoomRequest, UpdateRoomInput> {
    @Override
    protected Class<UpdateRoomInput> getTargetClass() {
        return UpdateRoomInput.class;
    }

    @Override
    protected UpdateRoomInput doConvert(UpdateRoomRequest source) {
        UpdateRoomInput input = UpdateRoomInput.builder()
                .roomId(source.getRoomId())
                .roomNo(source.getRoomNo())
                .beds(convertBeds(source))
                .floor(source.getFloor())
                .price(source.getPrice())
                .bathroomType(BathroomType.getByCode(source.getBathroomType().toString()))
                .build();

        return input;
    }

    private List<com.tinqinacademy.hotel.api.enumerations.BedSize> convertBeds(UpdateRoomRequest source) {
        List<com.tinqinacademy.hotel.api.enumerations.BedSize> bedSizes = source.getBeds().stream()
                .map(bedSize -> BedSize.getByCode(bedSize.toString()))
                .toList();
        return bedSizes;
    }
}

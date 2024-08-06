package com.tinqinacademy.bff.core.converters.impl;

import com.tinqinacademy.bff.api.operations.createroom.CreateRoomRequest;
import com.tinqinacademy.bff.core.converters.AbstractConverter;
import com.tinqinacademy.hotel.api.enumerations.BathroomType;
import com.tinqinacademy.hotel.api.enumerations.BedSize;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateRoomRequesstToCreateRoomInput extends AbstractConverter<CreateRoomRequest, CreateRoomInput> {
    @Override
    protected Class<CreateRoomInput> getTargetClass() {
        return CreateRoomInput.class;
    }

    @Override
    protected CreateRoomInput doConvert(CreateRoomRequest source) {
        CreateRoomInput target = CreateRoomInput
                .builder()
                .roomNo(source.getRoomNo())
                .bathroomType(BathroomType.getByCode(source.getBathroomType().toString()))
                .floor(source.getFloor())
                .price(source.getPrice())
                .beds(convertBeds(source))
                .build();
        return target;
    }

    private List<BedSize> convertBeds(CreateRoomRequest source) {
       List<BedSize> bedSizes = source.getBeds().stream()
                .map(bedSize -> BedSize.getByCode(bedSize.toString()))
                .toList();
        return bedSizes;
    }
}

package com.tinqinacademy.bff.core.converters.impl;

import com.tinqinacademy.bff.api.operations.partialupdateroom.PartialUpdateRoomRequest;
import com.tinqinacademy.bff.api.operations.updateroom.UpdateRoomRequest;
import com.tinqinacademy.bff.core.converters.AbstractConverter;
import com.tinqinacademy.hotel.api.enumerations.BathroomType;
import com.tinqinacademy.hotel.api.enumerations.BedSize;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.PartialUpdateRoomInput;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PartialUpdateRequestToPartialUpdateInput extends AbstractConverter<PartialUpdateRoomRequest, PartialUpdateRoomInput> {
    @Override
    protected Class<PartialUpdateRoomInput> getTargetClass() {
        return PartialUpdateRoomInput.class;
    }

    @Override
    protected PartialUpdateRoomInput doConvert(PartialUpdateRoomRequest source) {
        PartialUpdateRoomInput target = PartialUpdateRoomInput
                .builder()
                .roomId(source.getRoomId())
                .beds(convertBeds(source))
                .bathroomType(BathroomType.getByCode(source.getBathroomType().toString()))
                .floor(source.getFloor())
                .roomNo(source.getRoomNo())
                .price(source.getPrice())
                .build();
        return target;
    }

    private List<BedSize> convertBeds(PartialUpdateRoomRequest source) {
        List<com.tinqinacademy.hotel.api.enumerations.BedSize> bedSizes = source.getBeds().stream()
                .map(bedSize -> BedSize.getByCode(bedSize.toString()))
                .toList();
        return bedSizes;
    }
}

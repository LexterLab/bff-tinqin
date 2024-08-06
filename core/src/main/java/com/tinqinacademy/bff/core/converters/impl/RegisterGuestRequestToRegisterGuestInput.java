package com.tinqinacademy.bff.core.converters.impl;

import com.tinqinacademy.bff.api.operations.registerguest.RegisterGuestRequest;
import com.tinqinacademy.bff.core.converters.AbstractConverter;
import com.tinqinacademy.hotel.api.operations.registerguest.GuestInput;
import com.tinqinacademy.hotel.api.operations.registerguest.RegisterGuestInput;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RegisterGuestRequestToRegisterGuestInput extends AbstractConverter<RegisterGuestRequest, RegisterGuestInput> {
    @Override
    protected Class<RegisterGuestInput> getTargetClass() {
        return RegisterGuestInput.class;
    }

    @Override
    protected RegisterGuestInput doConvert(RegisterGuestRequest source) {
        RegisterGuestInput target = RegisterGuestInput.builder()
                .bookingId(source.getBookingId())
                .guests(convertGuests(source))
                .build();
        return target;
    }

    private List<GuestInput> convertGuests(RegisterGuestRequest source) {
        List<GuestInput> guests = new ArrayList<>();
        source.getGuests().forEach(guest -> guests.add(GuestInput
                .builder()
                .firstName(guest.getFirstName())
                .lastName(guest.getLastName())
                .birthday(guest.getBirthday())
                .idCardNo(guest.getIdCardNo())
                .idCardValidity(guest.getIdCardValidity())
                .idCardIssueAuthority(guest.getIdCardIssueAuthority())
                .idCardIssueDate(guest.getIdCardIssueDate())
                .build()));
        return guests;
    }
}

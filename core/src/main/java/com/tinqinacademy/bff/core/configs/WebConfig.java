package com.tinqinacademy.bff.core.configs;

import com.tinqinacademy.bff.core.converters.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final CreateRoomRequesstToCreateRoomInput createRoomRequesstToCreateRoomInput;
    private final UpdateRoomRequestToUpdateRoomInput updateRoomRequestToUpdateRoomInput;
    private final GetGuestReportOutputToGetGuestReportResponse getGuestReportOutputToGetGuestReportResponse;
    private final GetRoomOutputToGetRoomResponse getRoomOutputToGetRoomResponse;
    private final LeaveRoomCommentRequestToLeaveRoomCommentInput leaveRoomCommentRequestToLeaveRoomCommentInput;
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(createRoomRequesstToCreateRoomInput);
        registry.addConverter(updateRoomRequestToUpdateRoomInput);
        registry.addConverter(getGuestReportOutputToGetGuestReportResponse);
        registry.addConverter(getRoomOutputToGetRoomResponse);
        registry.addConverter(leaveRoomCommentRequestToLeaveRoomCommentInput);
    }
}

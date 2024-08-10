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
    private final PartialUpdateRequestToPartialUpdateInput partialUpdateRequestToPartialUpdateInput;
    private final RegisterGuestRequestToRegisterGuestInput registerGuestRequestToRegisterGuestInput;
    private final GetRoomCommentsOutputToGetRoomCommentsResponse getRoomCommentsOutputToGetRoomCommentsResponse;
    private final  EditUserCommentRequestToEditUserCommentInput editUserCommentRequestToEditUserCommentInput;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(createRoomRequesstToCreateRoomInput);
        registry.addConverter(updateRoomRequestToUpdateRoomInput);
        registry.addConverter(getGuestReportOutputToGetGuestReportResponse);
        registry.addConverter(getRoomOutputToGetRoomResponse);
        registry.addConverter(leaveRoomCommentRequestToLeaveRoomCommentInput);
        registry.addConverter(partialUpdateRequestToPartialUpdateInput);
        registry.addConverter(registerGuestRequestToRegisterGuestInput);
        registry.addConverter(getRoomCommentsOutputToGetRoomCommentsResponse);
        registry.addConverter(editUserCommentRequestToEditUserCommentInput);
    }
}

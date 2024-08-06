package com.tinqinacademy.bff.core.configs;

import com.tinqinacademy.bff.core.converters.impl.CreateRoomRequesstToCreateRoomInput;
import com.tinqinacademy.bff.core.converters.impl.UpdateRoomRequestToUpdateRoomInput;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final CreateRoomRequesstToCreateRoomInput createRoomRequesstToCreateRoomInput;
    private final UpdateRoomRequestToUpdateRoomInput updateRoomRequestToUpdateRoomInput;
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(createRoomRequesstToCreateRoomInput);
        registry.addConverter(updateRoomRequestToUpdateRoomInput);
    }
}

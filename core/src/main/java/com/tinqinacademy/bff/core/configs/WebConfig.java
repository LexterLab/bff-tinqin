package com.tinqinacademy.bff.core.configs;

import com.tinqinacademy.bff.core.converters.impl.CreateRoomOpInputToCreateRoomInput;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final CreateRoomOpInputToCreateRoomInput createRoomOpInputToCreateRoomInput;
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(createRoomOpInputToCreateRoomInput);
    }
}

package com.tinqinacademy.bff.core.configs;

import com.tinqinacademy.bff.core.converters.impl.DomainCreateRoomInputToCreateRoomInput;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final DomainCreateRoomInputToCreateRoomInput domainCreateRoomInputToCreateRoomInput;
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(domainCreateRoomInputToCreateRoomInput);
    }
}

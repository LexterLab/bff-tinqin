package com.tinqinacademy.bff.domain.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tinqinacademy.authentication.restexport.AuthenticationClient;
import com.tinqinacademy.bff.domain.deserialisers.UserDeserializer;
import com.tinqinacademy.comments.restexport.restexport.CommentClient;
import com.tinqinacademy.hotel.restexport.HotelClient;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;

@Configuration
public class RestClientConfig {
    @Value("${hotel.client.url}")
    private String hotelURL;

    @Value("${comments.client.url}")
    private String commentsURL;

    @Value("${authentication.client.url}")
    private String authenticationURL;

    @Bean
    public HotelClient getClient() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return Feign.builder()
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .target(HotelClient.class, hotelURL);
    }

    @Bean
    public CommentClient getCommentClient() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return Feign.builder()
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .target(CommentClient.class, commentsURL);
    }

    @Bean
    public AuthenticationClient getAuthenticationClient() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(User.class, new UserDeserializer());
        objectMapper.registerModule(module);
        return Feign.builder()
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .target(AuthenticationClient.class, authenticationURL);
    }
}

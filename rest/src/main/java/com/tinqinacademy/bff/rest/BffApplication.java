package com.tinqinacademy.bff.rest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.tinqinacademy.bff"})
@EnableFeignClients(basePackages = {"com.tinqinacademy.bff"})
public class BffApplication {
}

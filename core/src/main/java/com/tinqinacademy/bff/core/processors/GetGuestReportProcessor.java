package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.operations.getguestreport.GetGuestReport;
import com.tinqinacademy.hotel.api.operations.getguestreport.GetGuestReportInput;
import com.tinqinacademy.hotel.api.operations.getguestreport.GetGuestReportOutput;
import com.tinqinacademy.hotel.api.operations.registerguest.RegisterGuestOutput;
import com.tinqinacademy.hotel.restexport.HotelClient;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static io.vavr.API.Match;

@Service
@Slf4j
public class GetGuestReportProcessor extends BaseProcessor implements GetGuestReport {
    private final HotelClient hotelClient;
    public GetGuestReportProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
    }

    @Override
    public Either<ErrorOutput, GetGuestReportOutput> process(GetGuestReportInput input) {
        log.info("Start getGuestReport {}", input);
        return  Try.of(() -> {
            GetGuestReportOutput output = hotelClient.getGuestReport(input.getStartDate(), input.getEndDate(),
                    input.getFirstName(), input.getLastName(), input.getPhoneNo(), input.getIdCardNo(),
                    input.getIdCardValidity(), input.getIdCardIssueAuthority(), input.getIdCardIssueDate(),
                    input.getRoomNo());
            log.info("End getGuestReport {}", output);
            return output;
      }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

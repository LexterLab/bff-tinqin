package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReport;
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReportRequest;
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReportResponse;
import com.tinqinacademy.hotel.api.operations.getguestreport.GetGuestReportOutput;
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
    public Either<ErrorOutput, GetGuestReportResponse> process(GetGuestReportRequest request) {
        log.info("Start getGuestReport {}", request);
        return  Try.of(() -> {
            GetGuestReportOutput output = hotelClient.getGuestReport(request.getStartDate(), request.getEndDate(),
                    request.getFirstName(), request.getLastName(), request.getPhoneNo(), request.getIdCardNo(),
                    request.getIdCardValidity(), request.getIdCardIssueAuthority(), request.getIdCardIssueDate(),
                    request.getRoomNo());
            GetGuestReportResponse response = conversionService.convert(output, GetGuestReportResponse.class);
            log.info("End getGuestReport {}", response);
            return response;
      }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }
}

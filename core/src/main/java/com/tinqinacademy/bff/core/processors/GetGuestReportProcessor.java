package com.tinqinacademy.bff.core.processors;

import com.tinqinacademy.authentication.api.operations.finduser.FindUserInput;
import com.tinqinacademy.authentication.api.operations.finduser.FindUserOutput;
import com.tinqinacademy.authentication.restexport.AuthenticationClient;
import com.tinqinacademy.bff.api.errors.ErrorOutput;
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReport;
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReportRequest;
import com.tinqinacademy.bff.api.operations.getguestrerport.GetGuestReportResponse;
import com.tinqinacademy.bff.api.operations.getguestrerport.GuestOutput;
import com.tinqinacademy.hotel.api.operations.getguestreport.GetGuestReportOutput;
import com.tinqinacademy.hotel.restexport.HotelClient;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.vavr.API.Match;

@Service
@Slf4j
public class GetGuestReportProcessor extends BaseProcessor implements GetGuestReport {
    private final HotelClient hotelClient;
    private final AuthenticationClient authenticationClient;
    public GetGuestReportProcessor(ConversionService conversionService, Validator validator, HotelClient hotelClient,
                                   AuthenticationClient authenticationClient) {
        super(conversionService, validator);
        this.hotelClient = hotelClient;
        this.authenticationClient = authenticationClient;
    }

    @Override
    public Either<ErrorOutput, GetGuestReportResponse> process(GetGuestReportRequest request) {
        log.info("Start getGuestReport {}", request);
        return  Try.of(() -> {
            FindUserOutput findUserOutput = FindUserOutput.builder().build();
            if (request.getPhoneNo() != null) {
                 findUserOutput = authenticationClient
                        .findUser(FindUserInput.builder()
                                .phoneNo(request.getPhoneNo()).build());
            }

            GetGuestReportOutput output = hotelClient.getGuestReport(request.getStartDate(), request.getEndDate(),
                    request.getFirstName(), request.getLastName(),
                    findUserOutput.getUserId() == null ? null : findUserOutput.getUserId().toString(),
                    request.getIdCardNo(), request.getIdCardValidity(), request.getIdCardIssueAuthority(),
                    request.getIdCardIssueDate(), request.getRoomNo());

            GetGuestReportResponse response = conversionService.convert(output, GetGuestReportResponse.class);
            addUserToResponse(response, findUserOutput);
            log.info("End getGuestReport {}", response);
            return response;
      }).toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validatorCase(throwable),
                        feignCase(throwable),
                        defaultCase(throwable)
                ));
    }

    private void addUserToResponse(GetGuestReportResponse response, FindUserOutput findUserOutput) {
         response.getGuests()
                .forEach(guest -> guest.setUserId(findUserOutput.getUserId()));
    }
}

package com.shurona.holiday.infrastructure.client.datenager;

import com.shurona.holiday.infrastructure.client.datenager.dto.CountryCodeResponseDto;
import com.shurona.holiday.infrastructure.client.datenager.dto.PublicHolidayResponseDto;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface DateNagerApiClient {

    /**
     * 공휴일을 조회 가능한 국가 코드 및 국가 정보 갖고 오기
     */
    @GetExchange("/AvailableCountries")
    public List<CountryCodeResponseDto> findAvailableCountries();

    /**
     * 국가 및 연도별로 공휴일 갖고 오기
     */
    @GetExchange("/PublicHolidays/{year}/{countryCode}")
    public List<PublicHolidayResponseDto> findPublicHolidays(
        @PathVariable("year") int year,
        @PathVariable("countryCode") String countryCode
    );
}

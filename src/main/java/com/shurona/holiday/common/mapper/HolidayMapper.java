package com.shurona.holiday.common.mapper;

import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import com.shurona.holiday.infrastructure.client.datenager.dto.PublicHolidayResponseDto;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class HolidayMapper {

    /**
     * RestClient의 pulibc holiday의 Response를 Entity Holiday로 변환하는 매퍼
     */
    public Holiday publicHolidayResponseToHoliday(
        PublicHolidayResponseDto publicHoliday, Country country) {
        return Holiday.createHoliday(
            LocalDate.parse(publicHoliday.date()),
            publicHoliday.name(),
            publicHoliday.localName(),
            country,
            publicHoliday.fixed(),
            publicHoliday.global(),
            publicHoliday.launchYear(),
            publicHoliday.types(),
            publicHoliday.counties()
        );
    }

}

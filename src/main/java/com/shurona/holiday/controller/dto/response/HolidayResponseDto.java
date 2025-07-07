package com.shurona.holiday.controller.dto.response;

import com.shurona.holiday.domain.model.Holiday;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record HolidayResponseDto(
    Long id,
    LocalDate date,
    String localName,
    String name,
    String code,
    boolean fixed,
    boolean global,
    Integer launchYear,
    List<String> types,
    List<String> counties
) {

    public static HolidayResponseDto of(Holiday holiday) {
        return HolidayResponseDto.builder()
            .id(holiday.getId())
            .date(holiday.getDate())
            .localName(holiday.getLocalName())
            .name(holiday.getName())
            .code(holiday.getCountryCode().getCode())
            .fixed(holiday.isFixed())
            .global(holiday.isGlobal())
            .launchYear(holiday.getLaunchYear())
            .types(new ArrayList<>(holiday.getTypes()))
            .counties(new ArrayList<>(holiday.getCounties()))
            .build();
    }

}

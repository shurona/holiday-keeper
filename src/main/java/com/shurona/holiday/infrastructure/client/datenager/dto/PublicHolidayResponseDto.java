package com.shurona.holiday.infrastructure.client.datenager.dto;

import java.util.List;

/**
 * 외부 Api에서 공휴일 정보를 갖고 오는 ResponseDto
 */
public record PublicHolidayResponseDto(
    String date,
    String localName,
    String name,
    String countryCode,
    Boolean fixed,
    Boolean global,
    List<String> counties,
    Integer launchYear,
    List<String> types
) {

    public PublicHolidayResponseDto {
        counties = counties == null ? new java.util.ArrayList<>() : counties;
        types = types == null ? new java.util.ArrayList<>() : types;
    }


}
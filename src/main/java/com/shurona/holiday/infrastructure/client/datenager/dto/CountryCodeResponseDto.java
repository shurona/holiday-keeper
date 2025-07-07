package com.shurona.holiday.infrastructure.client.datenager.dto;

/**
 * 외부 api에서 지원하는 국가 정보 ResponseDto
 */
public record CountryCodeResponseDto(
    String countryCode,
    String name
) {

}

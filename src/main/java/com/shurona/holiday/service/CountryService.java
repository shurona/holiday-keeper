package com.shurona.holiday.service;

import com.shurona.holiday.domain.model.Country;

public interface CountryService {

    /**
     * 국가 코드를 기준으로 국가 정보 조회
     */
    public Country findByCountryCode(String code);

}

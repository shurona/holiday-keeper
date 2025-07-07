package com.shurona.holiday.domain;

import com.shurona.holiday.domain.model.Country;

public record HolidaySearchCondition(
    Integer year,
    Country country
) {

}

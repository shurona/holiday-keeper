package com.shurona.holiday.domain;

import com.shurona.holiday.domain.model.Country;
import java.time.LocalDate;

public record HolidaySearchCondition(
    Integer year,
    Country country,
    LocalDate from,
    LocalDate to,
    String type
) {

}

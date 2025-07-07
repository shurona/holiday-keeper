package com.shurona.holiday.domain;

import com.shurona.holiday.domain.model.Holiday;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public record HolidayKey(
    LocalDate date,
    String name,
    String localName,
    String code,
    Integer launchYear,
    Set<String> types,
    Set<String> counties
) {


    public static HolidayKey from(Holiday holiday) {
        return new HolidayKey(
            holiday.getDate(),
            holiday.getName(),
            holiday.getLocalName(),
            holiday.getCountryCode().getCode(),
            holiday.getLaunchYear(),
            new HashSet<>(holiday.getTypes()),
            new HashSet<>(holiday.getCounties()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HolidayKey that = (HolidayKey) o;
        return Objects.equals(date, that.date) &&
            Objects.equals(name, that.name) &&
            Objects.equals(localName, that.localName) &&
            Objects.equals(code, that.code) &&
            Objects.equals(launchYear, that.launchYear) &&
            Objects.equals(types, that.types) &&
            Objects.equals(counties, that.counties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, name, localName, code, launchYear, types, counties);
    }
}

package com.shurona.holiday.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.shurona.holiday.domain.HolidayKey;
import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import com.shurona.holiday.infrastructure.repository.HolidayQueryRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceMockTest {


    private Holiday koreaHoliday;

    @Mock
    private HolidayQueryRepository holidayQueryRepository;

    @InjectMocks
    private HolidayServiceImpl holidayService;


    private Country korea;
    private Country us;

    @BeforeEach
    public void setUp() {
        korea = Country.createCountry("KR", "Korea");
        us = Country.createCountry("US", "United states");

        koreaHoliday = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "New Year's Day", "New Year's Day", korea,
            true, true, null, List.of("Public"), List.of());

    }

    @Test
    public void 공휴일_동일_키_체크() {

        // given
        Holiday koreaHolidayCheck = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "New Year's Day", "New Year's Day", korea,
            true, false, null, List.of("Public"), List.of());

        Holiday fixedChange = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "New Year's Day", "New Year's Day", korea,
            false, false, null, List.of("Public"), List.of());

        Holiday sameData = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "New Year's Day", "New Year's Day", korea,
            true, true, null, List.of("Public"), List.of());

        // 기준키
        HolidayKey baseKey = HolidayKey.from(koreaHoliday);

        // when
        HolidayKey key2 = HolidayKey.from(sameData);
        HolidayKey globalChange = HolidayKey.from(koreaHolidayCheck);
        HolidayKey fixedChangeKey = HolidayKey.from(fixedChange);

        // then
        assertThat(baseKey.equals(key2)).isTrue();
        assertThat(baseKey.equals(globalChange)).isTrue();
        assertThat(baseKey.equals(fixedChangeKey)).isTrue();

    }

    @Test
    public void 공휴일_다른_키_체크() {

        // given
        Holiday diffYear = Holiday.createHoliday(
            LocalDate.of(2024, 1, 1), "New Year's Day", "New Year's Day", korea,
            true, false, null, List.of("Public"), List.of());

        Holiday diffMonth = Holiday.createHoliday(
            LocalDate.of(2023, 2, 1), "New Year's Day", "New Year's Day", korea,
            true, false, null, List.of("Public"), List.of());

        Holiday diffDay = Holiday.createHoliday(
            LocalDate.of(2023, 1, 11), "New Year's Day", "New Year's Day", korea,
            true, false, null, List.of("Public"), List.of());

        Holiday nameChange = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "Lunar", "New Year's Day", korea,
            false, false, null, List.of("Public"), List.of());

        Holiday localChange = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "New Year's Day", "Lunar", korea,
            false, false, null, List.of("Public"), List.of());

        Holiday codeChange = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "New Year's Day", "New Year's Day", us,
            true, true, null, List.of("Public"), List.of());

        Holiday typeChange = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "New Year's Day", "New Year's Day", korea,
            true, true, null, List.of("Bank", "School"), List.of());

        Holiday countiesChange = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "New Year's Day", "New Year's Day", korea,
            true, true, null, List.of("Public"),
            List.of("US-CA", "US-CT", "US-IL", "US-IN", "US-KY", "US-MI", "US-NY", "US-MO",
                "US-OH"));

        // 기준 키
        HolidayKey baseKey = HolidayKey.from(koreaHoliday);

        // when
        HolidayKey yearKey = HolidayKey.from(diffYear);
        HolidayKey monthKey = HolidayKey.from(diffMonth);
        HolidayKey dayKey = HolidayKey.from(diffDay);
        HolidayKey nameKey = HolidayKey.from(nameChange);
        HolidayKey localNameKey = HolidayKey.from(localChange);
        HolidayKey countryKey = HolidayKey.from(codeChange);
        HolidayKey typesKey = HolidayKey.from(typeChange);
        HolidayKey countiesKey = HolidayKey.from(countiesChange);

        // then
        // 날짜 변경 케이스
        assertThat(baseKey.equals(yearKey)).isFalse();
        assertThat(baseKey.equals(monthKey)).isFalse();
        assertThat(baseKey.equals(dayKey)).isFalse();

        // 이름 변경 케이스
        assertThat(baseKey.equals(nameKey)).isFalse();
        assertThat(baseKey.equals(localNameKey)).isFalse();

        // 국가 변경 케이스
        assertThat(baseKey.equals(countryKey)).isFalse();

        // 타입 및 카운티 변경 케이스
        assertThat(baseKey.equals(typesKey)).isFalse();
        assertThat(baseKey.equals(countiesKey)).isFalse();
    }

    @Test
    public void 평일_카운트() {
        // given
        LocalDate from = LocalDate.of(2023, 1, 1); // 일요일
        LocalDate to = LocalDate.of(2023, 1, 7);   // 토요일
        Country korea = Country.createCountry("KR", "Korea");

        // 공휴일 Mock 데이터 설정
        List<LocalDate> holidays = List.of(
            LocalDate.of(2023, 1, 2), // 월요일,
            LocalDate.of(2023, 1, 3) // 화요일
        );

        // HolidayQueryRepository가 공휴일 목록을 반환하도록 Mock 설정
        when(holidayQueryRepository.findLocalDateListBetweenFromToByCountry(from, to, korea))
            .thenReturn(holidays);

        // when
        int workingDays = holidayService.findWorkingDayBetweenFromToByCountry(from, to, korea);

        // then
        assertThat(workingDays).isEqualTo(3); // 화, 수, 목, 금 (공휴일 제외)
    }
}

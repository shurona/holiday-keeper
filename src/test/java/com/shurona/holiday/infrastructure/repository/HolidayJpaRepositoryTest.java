package com.shurona.holiday.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class HolidayJpaRepositoryTest {

    @Autowired
    private HolidayJpaRepository holidayJpaRepository;

    @Autowired
    private EntityManager em;

    private Country korea;
    private Country us;

    @BeforeEach
    void setUp() {
        korea = Country.createCountry("KR", "Korea");
        us = Country.createCountry("US", "United states");
        em.persist(korea);
        em.persist(us);

        // 2023년 한국 휴일
        Holiday koreaHoliday1 = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "신정", "New Year's Day", korea,
            true, true, null, List.of("Public"), List.of());
        Holiday koreaHoliday2 = Holiday.createHoliday(
            LocalDate.of(2023, 3, 1), "삼일절", "Independence Movement Day", korea,
            true, true, null, List.of("Public"), List.of());

        // 2023년 미국 휴일
        Holiday usHoliday1 = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "New Year's Day", "New Year's Day", us,
            true, true, null, List.of("Public"), List.of());
        Holiday usHoliday2 = Holiday.createHoliday(
            LocalDate.of(2023, 7, 4), "Independence Day", "Independence Day", us,
            true, true, null, List.of("Public"), List.of());

        // 2024년 한국 휴일
        Holiday koreaHoliday3 = Holiday.createHoliday(
            LocalDate.of(2024, 1, 1), "신정", "New Year's Day", korea,
            true, true, null, List.of("Public"), List.of());

        em.persist(koreaHoliday1);
        em.persist(koreaHoliday2);
        em.persist(usHoliday1);
        em.persist(usHoliday2);
        em.persist(koreaHoliday3);

        // entity manager 초기화
        em.flush();
        em.clear();
    }

    @Test
    public void 저정된_공휴일이_없는_경우() {
        // given
        // when
        Long ct = holidayJpaRepository.countHolidayByYearAndCountry(2025, korea);
        // then
        assertThat(ct).isEqualTo(0);
    }

    @Test
    public void 저장된_공휴일이_있는_경우() {
        //given
        //when
        Long ct = holidayJpaRepository.countHolidayByYearAndCountry(2023, us);

        // then
        assertThat(ct).isEqualTo(2);
    }

    @Test
    public void 공휴일_연도_삭제() {
        // given
        // when
        Integer deleteCt = holidayJpaRepository.deleteAllByYearAndCountry(2023, korea);
        Long deleteKoreaCt = holidayJpaRepository.countHolidayByYearAndCountry(2023, korea);
        Long existKoreaCt = holidayJpaRepository.countHolidayByYearAndCountry(2024, korea);

        // then
        assertThat(deleteCt).isEqualTo(2);
        assertThat(deleteKoreaCt).isEqualTo(0);
        assertThat(existKoreaCt).isEqualTo(1);
    }

}
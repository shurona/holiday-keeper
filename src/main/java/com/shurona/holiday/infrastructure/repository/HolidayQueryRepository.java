package com.shurona.holiday.infrastructure.repository;

import com.shurona.holiday.domain.HolidaySearchCondition;
import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayQueryRepository {

    /**
     * 조건에 맞는 공휴일 목록 갖고 오는 메소드
     */
    Page<Holiday> findAllHolidayByCondition(Pageable pageable,
        HolidaySearchCondition searchCondition);

    /**
     * 페이징 없이 연도와 국가에 맞는 공휴일 목록 갖고 온다.
     */
    List<Holiday> findAllHolidayByYearAndCountry(Integer year, Country country);

    /**
     * 조건에 맞는 공휴일 목록을 삭제 하는 메소드
     */
    long deleteAllByYearAndCountry(Integer year, Country country);

    /**
     * 특정 국가의 Working Day의 날짜를 구한다.
     */
    List<LocalDate> findLocalDateListBetweenFromToByCountry(
        LocalDate from, LocalDate to, Country country);
}

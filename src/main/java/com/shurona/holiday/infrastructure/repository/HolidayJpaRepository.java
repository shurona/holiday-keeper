package com.shurona.holiday.infrastructure.repository;

import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface HolidayJpaRepository extends JpaRepository<Holiday, Long> {

    @Query("select count(h) from Holiday h where EXTRACT(YEAR FROM h.date) = :year and h.countryCode = :country")
    Long countHolidayByYearAndCountry(Integer year, Country country);

    /**
     * 특정 년도와 국가의 공휴일 삭제
     */
    @Modifying
    @Query("delete from Holiday h where EXTRACT(YEAR FROM h.date) = :year and h.countryCode = :country")
    Integer deleteAllByYearAndCountry(Integer year, Country country);


    /**
     * 공휴일을 id 기준으로 삭제한다.
     */
    @Modifying
    @Query("delete from Holiday h where h.id in :ids")
    Integer deleteAllByIds(List<Long> ids);
}

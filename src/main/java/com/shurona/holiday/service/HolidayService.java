package com.shurona.holiday.service;

import com.shurona.holiday.domain.HolidaySearchCondition;
import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayService {

    /**
     * 조건에 맞는 공휴일 정보 갖고 오기
     */
    public Page<Holiday> findHollyDayList(
        Pageable pageable, HolidaySearchCondition searchCondition);

    /**
     * 연도와 국가에 해당하는 공휴일을 새로운 데이터로 업데이트 해준다.
     */
    public List<Holiday> upsertHoliday(Integer year, Country country);


    /**
     * 년도와 국가를 기준으로 삭제
     */
    public long deleteHolidayInfo(Integer year, Country country);

}

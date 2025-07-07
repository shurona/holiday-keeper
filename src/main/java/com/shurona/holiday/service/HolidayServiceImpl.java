package com.shurona.holiday.service;

import com.shurona.holiday.common.mapper.HolidayMapper;
import com.shurona.holiday.domain.HolidayKey;
import com.shurona.holiday.domain.HolidaySearchCondition;
import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import com.shurona.holiday.infrastructure.client.datenager.DateNagerApiClient;
import com.shurona.holiday.infrastructure.repository.HolidayJpaRepository;
import com.shurona.holiday.infrastructure.repository.HolidayQueryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class HolidayServiceImpl implements HolidayService {

    // repository
    private final HolidayQueryRepository holidayQueryRepository;
    private final HolidayJpaRepository holidayJpaRepository;

    // restClient
    private final DateNagerApiClient dateNagerApiClient;

    // mapper
    private final HolidayMapper holidayMapper;

    @Override
    public Page<Holiday> findHollyDayList(
        Pageable pageable, HolidaySearchCondition searchCondition) {

        return holidayQueryRepository.findAllHolidayByCondition(pageable, searchCondition);
    }

    @Transactional
    @Override
    public List<Holiday> upsertHoliday(Integer year, Country country) {
    
        List<Holiday> holidayList = holidayQueryRepository.findAllHolidayByYearAndCountry(
            year, country);

        // 공휴일 데이터 Fetch 해온다.
        List<Holiday> newHolidays = dateNagerApiClient.findPublicHolidays(year,
                country.getCode()).stream()
            .map(responseDto -> holidayMapper.publicHolidayResponseToHoliday(responseDto, country))
            .toList();

        // 기존의 공휴일을 Map 형식으로 처리한다.
        Map<HolidayKey, Holiday> existedHolidayMap = createHolidayKeyMap(holidayList);

        // 저장할 휴일 목록
        List<Holiday> holidaysForSave = new ArrayList<>();

        for (Holiday newHoliday : newHolidays) {
            HolidayKey key = HolidayKey.from(newHoliday);
            Holiday existingHoliday = existedHolidayMap.get(key);

            // 만약 Holiday키가 존재하는 지 확인한다.
            if (existingHoliday != null) {
                // 기존 ID 유지하며 업데이트
                updateHolidayFields(existingHoliday, newHoliday);
                holidaysForSave.add(existingHoliday);
                existedHolidayMap.remove(key); // 이후 삭제해준다.
            } else {
                // 새로운 휴일 추가
                holidaysForSave.add(newHoliday);
            }
        }

        // 남은 기존 휴일이 존재하면 삭제한다.
        if (!existedHolidayMap.isEmpty()) {
            holidayJpaRepository.deleteAllByIds(existedHolidayMap.values().stream()
                .map(Holiday::getId)
                .toList());
        }

        // 저장 및 반환
        return holidayJpaRepository.saveAll(holidaysForSave);
    }

    @Transactional
    @Override
    public long deleteHolidayInfo(Integer year, Country country) {

        return holidayQueryRepository.deleteAllByYearAndCountry(year, country);
    }

    // 휴일 목록을 키-값 맵으로 변환
    private Map<HolidayKey, Holiday> createHolidayKeyMap(List<Holiday> holidays) {
        return holidays.stream()
            .collect(Collectors.toMap(
                HolidayKey::from,
                Function.identity(),
                (existing, replacement) -> existing // 충돌 시 기존 항목 유지
            ));
    }

    // 기존 휴일 정보 업데이트
    private void updateHolidayFields(Holiday existingHoliday, Holiday newHoliday) {
        existingHoliday.updateNonKeyFields(newHoliday);
    }
}

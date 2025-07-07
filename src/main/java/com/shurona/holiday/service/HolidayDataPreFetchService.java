package com.shurona.holiday.service;

import com.shurona.holiday.common.mapper.HolidayMapper;
import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import com.shurona.holiday.infrastructure.client.datenager.DateNagerApiClient;
import com.shurona.holiday.infrastructure.client.datenager.dto.CountryCodeResponseDto;
import com.shurona.holiday.infrastructure.client.datenager.dto.PublicHolidayResponseDto;
import com.shurona.holiday.infrastructure.repository.CountryJpaRepository;
import com.shurona.holiday.infrastructure.repository.HolidayJpaRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 휴일의 데이터를 미리 갖고와서 저장하는 서비스 클래스
 */
@Profile({"!test"}) // test에서는 실행되지 않도록 함
@Slf4j
@RequiredArgsConstructor
@Service
public class HolidayDataPreFetchService {

    private static int MAX_RETRY = 3;
    private static int PRE_FETCH_YEAR_CT = 5;

    // repository
    private final HolidayJpaRepository holidayJpaRepository;
    private final CountryJpaRepository countryJpaRepository;

    // restClient
    private final DateNagerApiClient dateNagerApiClient;

    // mapper
    private final HolidayMapper holidayMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void preFetchHolidayData() {

        // 최근 5년도의 Fetch가 되지 않은 연도를 확인한다.
        List<Country> countryList = findCountryList();
        log.info("5년간의 국가 공휴일 데이터를 갖고 온다.");
        for (Country country : countryList) {
            int year = LocalDate.now().getYear();
            // 5년 간 데이터를 갖고 온다.
            for (int i = 0; i < PRE_FETCH_YEAR_CT; i++) {
                boolean success = false;
                int retryCount = 1;
                while (!success && retryCount <= MAX_RETRY) {
                    try {
                        success = saveHolidayDataByYear(year - i, country);
                    } catch (Exception e) {
                        retryCount++;
                        log.error("Year {} - 처리 중 오류 발생 (시도 {}/{}): {}",
                            year - i, retryCount, MAX_RETRY, e.getMessage());

                        if (retryCount > MAX_RETRY) {
                            log.error("Year {} - 최대 재시도 횟수 초과, 처리 실패", year - i);
                        }
                    }
                }
            }
            log.info("Country {} - 공휴일 데이터 갖고 오기 성공", country.getCode());
        }
        log.info("모든 공휴일 페치 성공");
    }


    /**
     * 연도 별로 국가의 공휴일을 저장한다.
     */
    @Transactional
    public boolean saveHolidayDataByYear(Integer year, Country country) {
        // Country - Year의 공휴일 정보가 있는지 확인한다.
        Long ct = holidayJpaRepository.countHolidayByYearAndCountry(year, country);
        // 만약 이미 존재하면 갖고 오지 않는다.
        if (ct > 0) {
            return true;
        }

        // 외부 api 호출
        List<PublicHolidayResponseDto> publicHolidays = dateNagerApiClient.findPublicHolidays(
            year, country.getCode());

        List<Holiday> holidays = publicHolidays.stream()
            .map(publicHoliday
                -> holidayMapper.publicHolidayResponseToHoliday(publicHoliday, country))
            .toList();
        holidayJpaRepository.saveAll(holidays);
        return true;
    }

    /**
     * 국가 정보를 조회한다.(데이터베이스에 없으면 api를 통해 받아온다.
     */
    @Transactional
    public List<Country> findCountryList() {
        List<Country> countryList = countryJpaRepository.findAll();
        if (countryList.isEmpty()) {
            List<CountryCodeResponseDto> availableCountries = dateNagerApiClient.findAvailableCountries();

            countryList = countryJpaRepository.saveAll(
                availableCountries.stream().map(
                    countryInfo -> Country.createCountry(countryInfo.countryCode(),
                        countryInfo.name())
                ).toList());
        }

        return countryList;
    }
}

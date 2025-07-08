package com.shurona.holiday.service.task;

import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.infrastructure.repository.CountryJpaRepository;
import com.shurona.holiday.service.HolidayService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnnualHolidaySyncScheduler {

    private final HolidayService holidayService;
    private final CountryJpaRepository countryJpaRepository;

    // 매년 1월 2일 01:00 KST에 실행
    @Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul")
    public void updateHolidayLastYearAndThisYear() {

        // 국가 목록을 갖고 온다.
        List<Country> countryList = countryJpaRepository.findAll();

        int year = LocalDate.now().getYear();
        // 국가별로 올해와 작년 공휴일을 업데이트 해준다.
        for (Country country : countryList) {
            for (int diff = 0; diff < 2; diff++) {
                holidayService.upsertHoliday(year - diff, country);
            }
        }

    }
}

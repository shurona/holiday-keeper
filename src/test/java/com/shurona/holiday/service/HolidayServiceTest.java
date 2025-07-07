package com.shurona.holiday.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import com.shurona.holiday.infrastructure.client.datenager.DateNagerApiClient;
import com.shurona.holiday.infrastructure.client.datenager.dto.PublicHolidayResponseDto;
import com.shurona.holiday.infrastructure.repository.HolidayQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class HolidayServiceTest {

    @Autowired
    private HolidayServiceImpl holidayService;

    @Autowired
    private HolidayQueryRepository holidayQueryRepository;

    @PersistenceContext
    private EntityManager em;

    @MockitoBean
    private DateNagerApiClient dateNagerApiClient;

    private Country korea;

    @BeforeEach
    void setUp() {
        korea = Country.createCountry("KR", "Korea");
        em.persist(korea);

        // 2023년 한국 휴일
        Holiday koreaHoliday1 = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "New Year's Day", "신정", korea,
            true, true, null, List.of("Public"), List.of());
        Holiday koreaHoliday2 = Holiday.createHoliday(
            LocalDate.of(2023, 3, 1), "Independence Movement Day", "삼일절", korea,
            true, true, null, List.of("Observance"), List.of());
        Holiday koreaHoliday3 = Holiday.createHoliday(
            LocalDate.of(2023, 1, 1), "New Year's Day", "신정", korea,
            true, false, null, List.of("Observance"), List.of("KR-KU"));

        em.persist(koreaHoliday1);
        em.persist(koreaHoliday2);
        em.persist(koreaHoliday3);

        // entity manager 초기화
        em.flush();
        em.clear();
    }

    @Test
    public void 리프레시_기본_테스트() {
        // given
        int targetYear = 2023;
        Country targetCountry = korea;

        List<PublicHolidayResponseDto> mockResponse = mockResponse();

        // Mock API 클라이언트 설정
        when(dateNagerApiClient.findPublicHolidays(targetYear, targetCountry.getCode()))
            .thenReturn(mockResponse);

        // when
        List<Holiday> result = holidayService.upsertHoliday(targetYear, targetCountry);

        List<Holiday> checkList = holidayQueryRepository.findAllHolidayByYearAndCountry(
            targetYear, targetCountry);

        // then
        // API 호출 확인
        verify(dateNagerApiClient).findPublicHolidays(targetYear, targetCountry.getCode());

        assertThat(checkList.size()).isEqualTo(3);
        assertThat(checkList.get(0).getName()).isEqualTo("New Year's Day");
        assertThat(checkList.get(0).isGlobal()).isFalse();
        assertThat(checkList.get(1).getTypes().contains("Public")).isTrue();
        assertThat(checkList.get(2).getCounties().size()).isEqualTo(0);

    }

    private List<PublicHolidayResponseDto> mockResponse() {
        // DateNager API가 반환할 모의 응답 데이터 생성
        return List.of(
            new PublicHolidayResponseDto(
                LocalDate.of(2023, 1, 1).toString(),
                "신정",
                "New Year's Day",
                "KR",
                false,
                false,
                List.of(),
                null,
                List.of("Public")
            ),
            new PublicHolidayResponseDto(
                LocalDate.of(2023, 3, 1).toString(),
                "삼일절",
                "Independence Movement Day",
                "KR",
                false,
                true,
                List.of(),
                null,
                List.of("Public")
            ),
            new PublicHolidayResponseDto(
                LocalDate.of(2023, 1, 21).toString(),
                "설날",
                "Lunar New Year",
                "KR",
                false,
                false,
                List.of(),
                null,
                List.of("Public")
            )
        );
    }

}
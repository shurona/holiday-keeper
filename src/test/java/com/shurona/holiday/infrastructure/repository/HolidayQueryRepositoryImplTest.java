package com.shurona.holiday.infrastructure.repository;

import static com.shurona.holiday.domain.HolidaySortType.DATE;
import static org.assertj.core.api.Assertions.assertThat;

import com.shurona.holiday.config.JpaTestConfig;
import com.shurona.holiday.domain.HolidaySearchCondition;
import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DataJpaTest
@Import({JpaTestConfig.class, HolidayQueryRepositoryImpl.class})
class HolidayQueryRepositoryImplTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HolidayQueryRepository holidayQueryRepository;

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

        // 2024년 미국 휴일
        Holiday usHoliday3 = Holiday.createHoliday(
            LocalDate.of(2024, 1, 15), "신정", "Martin Luther King, Jr. Day", us,
            true, true, null, List.of("Public"), List.of());

        Holiday usHoliday4 = Holiday.createHoliday(
            LocalDate.of(2024, 5, 8), "Truman Day", "Truman Day", us,
            false, false, null, List.of("School", "Authorities"), List.of("US-MO"));

        em.persist(koreaHoliday1);
        em.persist(koreaHoliday2);
        em.persist(koreaHoliday3);
        em.persist(usHoliday1);
        em.persist(usHoliday2);
        em.persist(usHoliday3);
        em.persist(usHoliday4);

        // entity manager 초기화
        em.flush();
        em.clear();
    }

    @Nested
    @DisplayName("조회 테스트")
    class queryTest {

        @Test
        void 연도_국가_휴일_검색() {
            // given
            HolidaySearchCondition condition = new HolidaySearchCondition(
                2023, korea, null, null, null);
            Pageable pageable = PageRequest.of(0, 10,
                Sort.by(DATE.toString()).ascending());

            // when
            Page<Holiday> result = holidayQueryRepository.findAllHolidayByCondition(pageable,
                condition);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getDate()).isEqualTo(LocalDate.of(2023, 1, 1));
            assertThat(result.getContent().get(1).getDate()).isEqualTo(LocalDate.of(2023, 3, 1));
        }

        @Test
        void 연도로만_휴일검색() {
            // given
            HolidaySearchCondition condition = new HolidaySearchCondition(
                2023, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10,
                Sort.by(DATE.toString()).ascending());

            // when
            Page<Holiday> result = holidayQueryRepository.findAllHolidayByCondition(pageable,
                condition);

            // then
            assertThat(result.getContent()).hasSize(4);
            assertThat(result.getContent()).allMatch(
                holiday -> holiday.getDate().getYear() == 2023);
        }

        @Test
        void 국가로만_휴일검색() {
            // given
            HolidaySearchCondition condition = new HolidaySearchCondition(
                null, korea, null, null, null);

            Pageable pageable = PageRequest.of(0, 10,
                Sort.by(DATE.toString()).ascending());

            // when
            Page<Holiday> result = holidayQueryRepository.findAllHolidayByCondition(pageable,
                condition);

            // then
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getContent()).allMatch(holiday ->
                holiday.getCountryCode().getId().equals(korea.getId()));
        }

        @Test
        void 조건없이_휴일검색() {
            // given
            HolidaySearchCondition condition = new HolidaySearchCondition(
                null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10,
                Sort.by(DATE.toString()).ascending());

            // when
            Page<Holiday> result = holidayQueryRepository.findAllHolidayByCondition(pageable,
                condition);

            // then
            assertThat(result.getContent()).hasSize(7);
        }

        @Test
        void 조건없이_페이징() {
            // given
            HolidaySearchCondition condition = new HolidaySearchCondition(
                null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 2,
                Sort.by(DATE.toString()).ascending());

            // when
            Page<Holiday> result = holidayQueryRepository.findAllHolidayByCondition(pageable,
                condition);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(7);
            assertThat(result.getTotalPages()).isEqualTo(4);
        }

        @Test
        void 조건없이_내림차순() {
            // given
            HolidaySearchCondition condition = new HolidaySearchCondition(
                null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10,
                Sort.by(DATE.toString()).descending());

            // when
            Page<Holiday> result = holidayQueryRepository.findAllHolidayByCondition(pageable,
                condition);

            // then
            assertThat(result.getContent()).hasSize(7);
            assertThat(result.getContent().get(0).getDate()).isEqualTo(LocalDate.of(2024, 5, 8));
        }

        @Test
        void 날짜_시작_조건_검색() {
            // given
            HolidaySearchCondition condition = new HolidaySearchCondition(
                null, null, LocalDate.parse("2023-01-02"), null, null);
            Pageable pageable = PageRequest.of(0, 10,
                Sort.by(DATE.toString()).ascending());

            // when
            Page<Holiday> result = holidayQueryRepository.findAllHolidayByCondition(pageable,
                condition);

            // then
            assertThat(result.getContent()).hasSize(5);
            assertThat(result.getContent().get(0).getDate()).isEqualTo(LocalDate.of(2023, 3, 1));
        }

        @Test
        void 날짜_끝_조건_검색() {
            // given
            HolidaySearchCondition condition = new HolidaySearchCondition(
                null, null, LocalDate.parse("2023-01-02"), LocalDate.parse("2024-01-01"), null);
            Pageable pageable = PageRequest.of(0, 10,
                Sort.by(DATE.toString()).descending());

            // when
            Page<Holiday> result = holidayQueryRepository.findAllHolidayByCondition(pageable,
                condition);

            // then
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getContent().get(0).getDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        }

        @Test
        void 공휴일_타입_검색() {
            // given
            HolidaySearchCondition condition = new HolidaySearchCondition(
                null, null, null, null, "School");
            Pageable pageable = PageRequest.of(0, 10,
                Sort.by(DATE.toString()).ascending());

            // when
            Page<Holiday> result = holidayQueryRepository.findAllHolidayByCondition(pageable,
                condition);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getDate()).isEqualTo(LocalDate.of(2024, 5, 8));
        }
    }

    @Nested
    @DisplayName("삭제 테스트")
    class deleteTest {

        @Test
        public void 연도_국가_삭제() {
            // given
            HolidaySearchCondition conditionDelete = new HolidaySearchCondition(
                2023, korea, null, null, null);
            HolidaySearchCondition conditionExist = new HolidaySearchCondition(
                2024, korea, null, null, null);
            Pageable pageable = PageRequest.of(0, 10,
                Sort.by(DATE.toString()).ascending());

            // when
            long deleteCt = holidayQueryRepository.deleteAllByYearAndCountry(2023, korea);
            Page<Holiday> deleteQueryCheck = holidayQueryRepository.findAllHolidayByCondition(
                pageable, conditionDelete);
            Page<Holiday> existQueryCheck = holidayQueryRepository.findAllHolidayByCondition(
                pageable, conditionExist);

            // then
            assertThat(deleteCt).isEqualTo(2);
            assertThat(deleteQueryCheck.toList().size()).isEqualTo(0);
            assertThat(existQueryCheck.toList().size()).isEqualTo(1);
        }

        @Test
        public void 부분_삭제() {
            // given
            HolidaySearchCondition conditionDelete = new HolidaySearchCondition(
                2023, korea, null, null, null);
            HolidaySearchCondition conditionExist = new HolidaySearchCondition(
                null, us, null, null, null);
            Pageable pageable = PageRequest.of(0, 10,
                Sort.by(DATE.toString()).ascending());

            // when
            long delete2023Ct = holidayQueryRepository.deleteAllByYearAndCountry(2023, null);
            Page<Holiday> deleteQueryCheck = holidayQueryRepository.findAllHolidayByCondition(
                pageable, conditionDelete);

            long deleteUsCt = holidayQueryRepository.deleteAllByYearAndCountry(null, us);
            Page<Holiday> existQueryCheck = holidayQueryRepository.findAllHolidayByCondition(
                pageable, conditionExist);

            // then
            assertThat(delete2023Ct).isEqualTo(4);
            assertThat(deleteUsCt).isEqualTo(2);
            assertThat(deleteQueryCheck.toList().size()).isEqualTo(0);
            assertThat(existQueryCheck.toList().size()).isEqualTo(0);
        }

        @Test
        public void 전체_삭제() {
            // given
            HolidaySearchCondition condition = new HolidaySearchCondition(
                null, null, null, null, null);
            Pageable pageable = PageRequest.of(0, 10,
                Sort.by(DATE.toString()).ascending());

            // when
            long ct = holidayQueryRepository.deleteAllByYearAndCountry(null, null);
            Page<Holiday> holidays = holidayQueryRepository.findAllHolidayByCondition(
                pageable, condition);

            // then
            assertThat(ct).isEqualTo(7);
            assertThat(holidays.toList().size()).isEqualTo(0);
        }
    }
}
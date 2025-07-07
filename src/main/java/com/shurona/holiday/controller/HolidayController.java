package com.shurona.holiday.controller;

import com.shurona.holiday.common.exception.HolidayErrorCode;
import com.shurona.holiday.common.exception.HolidayException;
import com.shurona.holiday.common.response.ApiResponse;
import com.shurona.holiday.common.response.PageResponse;
import com.shurona.holiday.controller.dto.request.HolidayDeleteRequestDto;
import com.shurona.holiday.controller.dto.request.HolidaySearchConditionRequestDto;
import com.shurona.holiday.controller.dto.response.HolidayResponseDto;
import com.shurona.holiday.domain.HolidaySearchCondition;
import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import com.shurona.holiday.service.CountryService;
import com.shurona.holiday.service.HolidayService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/holidays")
@RestController
public class HolidayController {

    private final HolidayService holidayService;
    private final CountryService countryService;

    /**
     *
     */
    @GetMapping
    public ApiResponse<PageResponse<HolidayResponseDto>> findHolidayList(
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @ModelAttribute HolidaySearchConditionRequestDto condition
    ) {

        // 국가 코드가 존재하는 지 확인한다.
        Country country = findCountryByCode(condition.code());

        // 페이지 정보 조회
        Page<Holiday> holiDayList = holidayService.findHollyDayList(pageable,
            new HolidaySearchCondition(condition.year(), country));

        return ApiResponse.success(
            PageResponse.from(holiDayList.map(HolidayResponseDto::of))
        );
    }

    @PostMapping("/refresh")
    public ApiResponse<List<HolidayResponseDto>> refreshHolidayDate(
        @RequestBody HolidayDeleteRequestDto requestDto
    ) {

        // 국가 코드가 존재하는 지 확인한다.
        Country country = countryService.findByCountryCode(requestDto.code());
        List<Holiday> holidays = holidayService.upsertHoliday(requestDto.year(), country);

        return ApiResponse.success(holidays.stream().map(HolidayResponseDto::of).toList());
    }

    /**
     *
     */
    @DeleteMapping
    public ApiResponse<Void> deleteHolidayList(
        @RequestBody HolidayDeleteRequestDto requestDto
    ) {

        // 국가 코드가 존재하는 지 확인한다.
        Country country = findCountryByCode(requestDto.code());

        // 공휴일 정보 삭제
        holidayService.deleteHolidayInfo(requestDto.year(), country);

        return ApiResponse.success(null);
    }

    /**
     * Country 조회 없으면 에러
     */
    private Country findCountryByCode(String code) {
        Country country = countryService.findByCountryCode(code);
        // 국가 코드가 존재하지만 country가 null 이면 잘못된 입력
        if (code != null && country == null) {
            throw new HolidayException(HolidayErrorCode.COUNTRY_INVALID_INPUT);
        }

        return country;
    }


}

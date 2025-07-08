package com.shurona.holiday.controller;

import static com.shurona.holiday.common.exception.HolidayErrorCode.INVALID_SORT_TYPE;

import com.shurona.holiday.common.exception.HolidayErrorCode;
import com.shurona.holiday.common.exception.HolidayException;
import com.shurona.holiday.common.response.ApiResponse;
import com.shurona.holiday.common.response.PageResponse;
import com.shurona.holiday.controller.dto.request.HolidayDeleteRequestDto;
import com.shurona.holiday.controller.dto.request.HolidaySearchConditionRequestDto;
import com.shurona.holiday.controller.dto.response.HolidayResponseDto;
import com.shurona.holiday.domain.HolidaySearchCondition;
import com.shurona.holiday.domain.HolidaySortType;
import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import com.shurona.holiday.service.CountryService;
import com.shurona.holiday.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/holidays")
@RestController
public class HolidayController {

    private final HolidayService holidayService;
    private final CountryService countryService;

    @Operation(
        summary = "휴일 목록 조회",
        description = "연도와 국가 코드를 기준으로 휴일 목록을 조회합니다."
    )
    @GetMapping
    public ApiResponse<PageResponse<HolidayResponseDto>> findHolidayList(
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "DATE") String sort,
        @RequestParam(defaultValue = "ASC") String direction,
        @ModelAttribute HolidaySearchConditionRequestDto condition
    ) {

        // 국가 코드가 존재하는 지 확인한다.
        Country country = findCountryByCode(condition.code());

        // 정렬 정보
        PageRequest pageRequest = settingHolidayPageRequest(
            size, page, sort, direction);

        // 페이지 정보 조회
        Page<Holiday> holiDayList = holidayService.findHollyDayList(pageRequest,
            new HolidaySearchCondition(condition.year(), country));

        return ApiResponse.success(
            PageResponse.from(holiDayList.map(HolidayResponseDto::of))
        );
    }

    @Operation(
        summary = "휴일 목록 갱신",
        description = "연도와 국가 코드를 기준으로 휴일 목록을 갱신합니다."
    )
    @PostMapping("/refresh")
    public ApiResponse<List<HolidayResponseDto>> refreshHolidayDate(
        @RequestBody HolidayDeleteRequestDto requestDto
    ) {

        // 국가 코드가 존재하는 지 확인한다.
        Country country = countryService.findByCountryCode(requestDto.code());
        List<Holiday> holidays = holidayService.upsertHoliday(requestDto.year(), country);

        return ApiResponse.success(holidays.stream().map(HolidayResponseDto::of).toList());
    }

    @Operation(
        summary = "휴일 목록 삭제",
        description = "연도와 국가 코드을 기준으로 휴일 목록을 삭제합니다."
    )
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

    /**
     * 기본적인 페이징 정보 생성
     */
    private PageRequest settingHolidayPageRequest(
        Integer size, Integer page, String sort, String direction) {

        // sort가 HolidaySortType에 속하는 지 확인한다.
        HolidaySortType sortType;
        try {
            sortType = HolidaySortType.valueOf(sort.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new HolidayException(INVALID_SORT_TYPE);
        }

        // 정렬 방식을 저장하고 PageRequest를 만든다.
        Sort sortInfo =
            direction.equalsIgnoreCase("DESC") ?
                Sort.by(Order.desc(sortType.toString()))
                : Sort.by(Order.asc(sortType.toString()));
        int pageNum = Math.max(0, page);
        int sizeNum = Math.min(Math.max(1, size), 100); // size의 크기 1과 100 사이
        return PageRequest.of(pageNum, sizeNum, sortInfo);
    }
}

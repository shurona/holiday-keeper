package com.shurona.holiday.infrastructure.repository;

import static com.shurona.holiday.domain.model.QHoliday.holiday;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shurona.holiday.domain.HolidaySearchCondition;
import com.shurona.holiday.domain.HolidaySortType;
import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.domain.model.Holiday;
import com.shurona.holiday.infrastructure.repository.dto.HolidayDateDao;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class HolidayQueryRepositoryImpl implements HolidayQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Page<Holiday> findAllHolidayByCondition(
        Pageable pageable, HolidaySearchCondition searchCondition) {

        BooleanBuilder builder = new BooleanBuilder();

        if (searchCondition.year() != null) {
            builder.and(holiday.date.year().eq(searchCondition.year()));
        }

        if (searchCondition.country() != null) {
            builder.and(holiday.countryCode.id.eq(searchCondition.country().getId()));
        }

        if (searchCondition.from() != null) {
            builder.and(holiday.date.goe(searchCondition.from()));

        }

        if (searchCondition.to() != null) {
            builder.and(holiday.date.loe(searchCondition.to()));
        }

        if (searchCondition.type() != null) {
            // 타입에 대소문자를 구분하지 않는다.
            String typeToSearch = searchCondition.type().toLowerCase();
            builder.and(holiday.types.any().toLowerCase().eq(typeToSearch));
        }

        // 먼저 id 목록을 먼저 불러온다.
        List<Long> holidayIds = query.select(holiday.id)
            .from(holiday)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(sortHoliday(pageable).toArray(OrderSpecifier[]::new))
            .fetch();

        // ID가 없으면 빈 결과 반환
        if (holidayIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // 가져온 ID 목록으로 실제 데이터를 로드한다.
        List<Holiday> fetch = query.selectFrom(holiday)
            .distinct()
            .leftJoin(holiday.counties).fetchJoin()
            .leftJoin(holiday.types).fetchJoin()
            .where(holiday.id.in(holidayIds))
            .orderBy(sortHoliday(pageable).toArray(OrderSpecifier[]::new))
            .fetch();

        JPAQuery<Long> total = query.select(holiday.count())
            .from(holiday)
            .where(builder);

        return PageableExecutionUtils.getPage(fetch, pageable, total::fetchOne);
    }

    @Override
    public List<Holiday> findAllHolidayByYearAndCountry(Integer year, Country country) {
        BooleanBuilder builder = new BooleanBuilder();

        if (year != null) {
            builder.and(holiday.date.year().eq(year));
        }

        if (country != null) {
            builder.and(holiday.countryCode.id.eq(country.getId()));
        }

        return query.selectFrom(holiday)
            .leftJoin(holiday.counties).fetchJoin()
            .leftJoin(holiday.types).fetchJoin()
            .orderBy(new OrderSpecifier<>(Order.ASC, holiday.date))
            .where(builder)
            .fetch();
    }

    @Override
    public long deleteAllByYearAndCountry(Integer year, Country country) {
        BooleanBuilder builder = new BooleanBuilder();

        if (year != null) {
            builder.and(holiday.date.year().eq(year));
        }

        if (country != null) {
            builder.and(holiday.countryCode.id.eq(country.getId()));
        }

        return query.delete(holiday)
            .where(builder)
            .execute();
    }

    @Override
    public List<LocalDate> findLocalDateListBetweenFromToByCountry(
        LocalDate from, LocalDate to, Country country) {

        BooleanBuilder builder = new BooleanBuilder();

        if (from != null) {
            builder.and(holiday.date.goe(from));

        }

        if (to != null) {
            builder.and(holiday.date.loe(to));
        }

        if (country != null) {
            builder.and(holiday.countryCode.id.eq(country.getId()));
        }

        return query.select(
                Projections.constructor(HolidayDateDao.class,
                    holiday.date
                )
            )
            .from(holiday)
            .orderBy(new OrderSpecifier<>(Order.ASC, holiday.date))
            .where(builder)
            .fetch().stream().map(HolidayDateDao::date).toList();
    }

    /**
     * 공휴일 정렬 처리 함수
     */
    private List<OrderSpecifier> sortHoliday(Pageable pageable) {

        if (pageable.getSort().isEmpty()) {
            return List.of(new OrderSpecifier<>(Order.ASC, holiday.date));
        }

        return pageable.getSort().map(order -> {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
            HolidaySortType sortType = HolidaySortType.valueOf(order.getProperty());

            // sortType enum에 따른 정렬 처리
            switch (sortType) {
                case NAME:
                    return new OrderSpecifier(direction, holiday.name);
                case COUNTRY:
                    return new OrderSpecifier(direction, holiday.countryCode);
                case DATE:
                default:
                    return new OrderSpecifier(direction, holiday.date);
            }
        }).toList();
    }
}

package com.shurona.holiday.domain.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.shurona.holiday.common.entity.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name = "holiday")
public class Holiday extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "local_name", nullable = false)
    private String localName;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code_id")
    private Country countryCode;

    @Column(name = "fixed", nullable = false)
    private boolean fixed;

    @Column(name = "global_flag", nullable = false)
    private boolean global;

    @Column(name = "launch_year")
    private Integer launchYear;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "holiday_type",
        joinColumns = @JoinColumn(name = "holiday_id"))
    @Column(name = "type", length = 100)
    private Set<String> types = new HashSet<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "holiday_county",
        joinColumns = @JoinColumn(name = "holiday_id"))
    @Column(name = "country", length = 100)
    private Set<String> counties = new HashSet<>();

    public static Holiday createHoliday(
        LocalDate date, String name, String localName, Country country,
        boolean fixed, boolean global, Integer launchYear,
        List<String> types, List<String> countries
    ) {
        return Holiday.builder()
            .date(date)
            .name(name)
            .localName(localName)
            .countryCode(country)
            .fixed(fixed)
            .global(global)
            .launchYear(launchYear)
            .types(new HashSet<>(types))
            .counties(new HashSet<>(countries))
            .build();
    }

    public void updateNonKeyFields(Holiday newHoliday) {
        this.global = newHoliday.global;
    }
}

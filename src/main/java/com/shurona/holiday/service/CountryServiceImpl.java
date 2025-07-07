package com.shurona.holiday.service;

import com.shurona.holiday.domain.model.Country;
import com.shurona.holiday.infrastructure.repository.CountryJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class CountryServiceImpl implements CountryService {

    private final CountryJpaRepository countryRepository;

    @Override
    public Country findByCountryCode(String code) {

        Optional<Country> country = countryRepository.findByCode(code);

        return country.orElse(null);
    }
}

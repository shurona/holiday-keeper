package com.shurona.holiday.infrastructure.repository;

import com.shurona.holiday.domain.model.Country;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryJpaRepository extends JpaRepository<Country, Long> {
    
    List<Country> findAllByCode(String code);

    Optional<Country> findByCode(String code);
}

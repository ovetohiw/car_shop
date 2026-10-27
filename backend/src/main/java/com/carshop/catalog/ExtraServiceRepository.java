package com.carshop.catalog;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtraServiceRepository extends JpaRepository<ExtraService, Long> {

  Optional<ExtraService> findByCode(String code);
}

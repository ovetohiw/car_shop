package com.carshop.catalog;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorOptionRepository extends JpaRepository<ColorOption, Long> {

  Optional<ColorOption> findByCode(String code);
}

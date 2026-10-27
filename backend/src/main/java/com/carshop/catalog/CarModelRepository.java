package com.carshop.catalog;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarModelRepository extends JpaRepository<CarModel, Long> {

  Optional<CarModel> findByCode(String code);
}

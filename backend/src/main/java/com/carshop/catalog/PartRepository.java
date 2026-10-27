package com.carshop.catalog;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long> {

  Optional<Part> findBySku(String sku);
}

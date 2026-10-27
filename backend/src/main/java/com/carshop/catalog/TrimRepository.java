package com.carshop.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrimRepository extends JpaRepository<Trim, Long> {

  List<Trim> findByCarModelId(Long carModelId);
}

package com.carshop.seed;

import com.carshop.catalog.CarModelRepository;
import com.carshop.catalog.ColorOptionRepository;
import com.carshop.catalog.ExtraServiceRepository;
import com.carshop.catalog.PartRepository;
import com.carshop.catalog.TrimRepository;
import com.carshop.user.Role;
import com.carshop.user.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedIntegrityChecker implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(SeedIntegrityChecker.class);

  private final UserAccountRepository userAccountRepository;
  private final CarModelRepository carModelRepository;
  private final TrimRepository trimRepository;
  private final ColorOptionRepository colorOptionRepository;
  private final ExtraServiceRepository extraServiceRepository;
  private final PartRepository partRepository;

  public SeedIntegrityChecker(
      UserAccountRepository userAccountRepository,
      CarModelRepository carModelRepository,
      TrimRepository trimRepository,
      ColorOptionRepository colorOptionRepository,
      ExtraServiceRepository extraServiceRepository,
      PartRepository partRepository) {
    this.userAccountRepository = userAccountRepository;
    this.carModelRepository = carModelRepository;
    this.trimRepository = trimRepository;
    this.colorOptionRepository = colorOptionRepository;
    this.extraServiceRepository = extraServiceRepository;
    this.partRepository = partRepository;
  }

  @Override
  public void run(ApplicationArguments args) {
    long staffCount = userAccountRepository.countByRole(Role.STAFF);
    long carModels = carModelRepository.count();
    long trims = trimRepository.count();
    long colors = colorOptionRepository.count();
    long extras = extraServiceRepository.count();
    long parts = partRepository.count();

    if (staffCount < 1 || carModels < 1 || trims < 1 || colors < 1 || extras < 1 || parts < 1) {
      throw new IllegalStateException(
          "Demo seeds incomplete: staff=%d cars=%d trims=%d colors=%d extras=%d parts=%d"
              .formatted(staffCount, carModels, trims, colors, extras, parts));
    }

    log.info(
        "Demo seeds readable: staff={} cars={} trims={} colors={} extras={} parts={}",
        staffCount,
        carModels,
        trims,
        colors,
        extras,
        parts);
  }
}

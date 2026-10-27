package com.carshop.staff;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff")
public class StaffProbeController {

  @GetMapping("/ping")
  public Map<String, String> ping() {
    return Map.of("status", "ok", "scope", "staff");
  }
}

package com.my.foody.domain.order.service.timepro;

import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class TimeProvider {
  public LocalTime now() {
    return LocalTime.now();
  }
}

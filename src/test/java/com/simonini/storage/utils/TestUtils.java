package com.simonini.storage.utils;

import java.time.LocalDate;

public class TestUtils {

  public static LocalDate getBirthDate(Long years) {
    return LocalDate.now().minusYears(years);
  }
}

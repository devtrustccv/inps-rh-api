package cv.inps.rh.shared.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

public class NumberUtils {

  private NumberUtils() {
  }

  public static BigDecimal sum(BigDecimal... values) {
    return Arrays.stream(values)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}

package cv.inps.rh.shared.util;

import java.util.Arrays;
import java.util.Objects;

public class NumberUtils {

  private NumberUtils() {
  }

  public static Long sum(Long... values) {
    return Arrays.stream(values).filter(Objects::nonNull).reduce(0L, Long::sum);
  }

}

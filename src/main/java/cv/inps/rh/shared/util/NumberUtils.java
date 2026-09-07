package cv.inps.rh.shared.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class NumberUtils {

  private NumberUtils() {
  }

  public static DecimalFormat dotDecimalFormat() {
    var symbols = new DecimalFormatSymbols(Locale.US);
    symbols.setGroupingSeparator('.');
    return new DecimalFormat("#,###", symbols);
  }

  public static DecimalFormat spaceDecimalFormat() {
    var symbols = new DecimalFormatSymbols(Locale.US);
    symbols.setGroupingSeparator(' ');
    return new DecimalFormat("#,###", symbols);
  }

  public static BigDecimal sum(BigDecimal... values) {
    return Arrays.stream(values)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}

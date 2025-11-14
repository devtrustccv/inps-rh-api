package cv.inps.rh.shared.util;

public final class Utils {

  private Utils() {
  }

  public static Integer parseFlag(String value) {
    return value != null ? Integer.parseInt(value) : null;
  }
}

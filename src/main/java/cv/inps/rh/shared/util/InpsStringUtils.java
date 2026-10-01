package cv.inps.rh.shared.util;

import java.text.Normalizer;

public final class InpsStringUtils {

  private InpsStringUtils() {
  }

  public static String normalizeText(String text) {

    if (text == null)
      return null;

    var normalized = text.trim().replaceAll("\\s+", " ");

    normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

    return normalized;
  }

}

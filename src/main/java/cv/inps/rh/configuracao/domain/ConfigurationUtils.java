package cv.inps.rh.configuracao.domain;

import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ConfigurationUtils {

  public static Integer parseFlag(String value) {
    return StringUtils.hasText(value) ? Integer.parseInt(value) : null;
  }

  public static PageRequest buildDefaultPageRequest(Map<String, String> filters) {

    var page = filters.getOrDefault("page", "0");
    var size = filters.getOrDefault("size", "30");

    return PageRequest.of(
        Integer.parseInt(page),
        Integer.parseInt(size)
    );
  }


  public static String normalizeAndSetToLowerCaseText(String input) {

    if (Objects.isNull(input))
      return null;

    var normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
    var pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    return pattern.matcher(normalized).replaceAll("").toLowerCase();
  }

}

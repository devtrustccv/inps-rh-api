package cv.inps.rh.configuracao.domain;

import org.springframework.data.domain.PageRequest;

import java.util.Map;

public class ConfigurationUtils {

  public static Integer parseFlag(String value) {
    return value != null ? Integer.parseInt(value) : null;
  }

  public static PageRequest buildDefaultPageRequest(Map<String, String> filters) {

    var page = filters.getOrDefault("page", "0");
    var size = filters.getOrDefault("size", "30");

    return PageRequest.of(
        Integer.parseInt(page),
        Integer.parseInt(size)
    );
  }

}

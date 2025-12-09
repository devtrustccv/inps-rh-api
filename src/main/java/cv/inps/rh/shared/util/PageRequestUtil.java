package cv.inps.rh.shared.util;

import org.springframework.data.domain.PageRequest;

public final class PageRequestUtil {

  private PageRequestUtil() {
  }

  public static PageRequest buildPageRequest(String page, String size) {
    return PageRequest.of(
        Integer.parseInt(page),
        Integer.parseInt(size)
    );
  }


}

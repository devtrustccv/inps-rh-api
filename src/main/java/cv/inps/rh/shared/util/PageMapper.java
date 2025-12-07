package cv.inps.rh.shared.util;

import cv.inps.rh.shared.application.dto.PageDTO;
import org.springframework.data.domain.Page;

public final class PageMapper {

  private PageMapper() {
  }

  public static <T extends PageDTO> void fillPagination(Page<?> page, T target) {
    target.setPageNumber(page.getNumber());
    target.setPageSize(page.getSize());
    target.setTotalElements(page.getTotalElements());
    target.setTotalPages(page.getTotalPages());
    target.setFirst(page.isFirst());
    target.setLast(page.isLast());
  }

}

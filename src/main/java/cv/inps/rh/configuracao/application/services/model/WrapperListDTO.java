package cv.inps.rh.configuracao.application.services.model;

import cv.inps.rh.shared.application.dto.PageDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WrapperListDTO extends PageDTO {

  private List<?> content;

}

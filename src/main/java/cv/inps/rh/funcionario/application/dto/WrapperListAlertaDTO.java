package cv.inps.rh.funcionario.application.dto;

import cv.inps.rh.shared.application.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class WrapperListAlertaDTO extends PageDTO {
    private List<AlertaDTO> content;
}

package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.missaoservico.application.dto.MissaoLogisticaRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveMissaoServicoLogisticaCommand implements Command {

  
  private MissaoLogisticaRequestDTO missaologisticarequest;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}
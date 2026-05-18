package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.processamento.application.dto.AumentoSalarialRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAumentoSalarialCommand implements Command {


  private AumentoSalarialRequestDTO aumentosalarialrequest;
  @NotBlank(message = "The field <aumentoSalarialId> is required")
  private String aumentoSalarialId;

}

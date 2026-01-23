package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarFaltaCommand implements Command {

  
  private FaltaReqDTO faltareq;
  @NotBlank(message = "The field <faltaId> is required")
  private String faltaId;

}
package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.missaoservico.application.dto.MissaoCancelarRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelarMissaoServicoCommand implements Command {

  
  private MissaoCancelarRequestDTO missaocancelarrequest;
  @NotBlank(message = "The field <id> is required")
  private String id;

}
package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.missaoservico.application.dto.MissaoAutorizacaoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveMissaoServicoAutorizacaoCommand implements Command {

  
  private MissaoAutorizacaoRequestDTO missaoautorizacaorequest;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}
package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.missaoservico.application.dto.MissaoAnaliseRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvancarAnaliseProcessoMissaoServicoCommand implements Command {

  
  private MissaoAnaliseRequestDTO missaoanaliserequest;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}
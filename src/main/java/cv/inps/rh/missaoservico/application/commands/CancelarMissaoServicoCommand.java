package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.missaoservico.application.dto.MissaoCancelarRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelarMissaoServicoCommand implements Command {


  private MissaoCancelarRequestDTO missaocancelarrequest;
  @NotBlank(message = "The field <id> is required")
  private String id;

}

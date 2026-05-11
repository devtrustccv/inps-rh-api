package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.missaoservico.application.dto.MissaoPagamentoRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveMissaoServicoPagamentoCommand implements Command {


  private MissaoPagamentoRequestDTO missaopagamentorequest;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}

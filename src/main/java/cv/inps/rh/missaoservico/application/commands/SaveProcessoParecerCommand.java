package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.missaoservico.application.dto.ParecerRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Parecer de uma etapa de validação do processo — {@code etapa} = VALIDACAO_UGAL | APROVACAO_RH. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveProcessoParecerCommand implements Command {

  private ParecerRequestDTO parecerrequest;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;
  @NotBlank(message = "The field <tipoProcesso> is required")
  private String tipoProcesso;
  @NotBlank(message = "The field <etapa> is required")
  private String etapa;

}

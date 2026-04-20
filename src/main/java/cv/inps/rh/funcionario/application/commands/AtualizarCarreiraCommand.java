package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarCarreiraCommand implements Command {

  
  private DadosContratuaisReqDTO dadoscontratuaisreq;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;
  @NotBlank(message = "The field <carreiraId> is required")
  private String carreiraId;

}
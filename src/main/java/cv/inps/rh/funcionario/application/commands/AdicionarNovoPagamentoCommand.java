package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.NovoPagamentoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdicionarNovoPagamentoCommand implements Command {

  
  private NovoPagamentoRequestDTO novopagamentorequest;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}
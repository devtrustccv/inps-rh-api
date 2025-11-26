package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.NovoPagamentoRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdicionarNovoPagamentoCommand implements Command {


  private NovoPagamentoRequestDTO novopagamentorequest;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}

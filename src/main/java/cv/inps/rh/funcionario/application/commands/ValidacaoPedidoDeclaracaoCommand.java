package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoValidacaoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidacaoPedidoDeclaracaoCommand implements Command {

  
  private PedidoDeclaracaoValidacaoDTO pedidodeclaracaovalidacao;
  @NotBlank(message = "The field <id> is required")
  private String id;

}
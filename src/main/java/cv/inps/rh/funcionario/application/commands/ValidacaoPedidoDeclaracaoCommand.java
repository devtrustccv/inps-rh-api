package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoValidacaoDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidacaoPedidoDeclaracaoCommand implements Command {


  private PedidoDeclaracaoValidacaoDTO pedidodeclaracaovalidacao;
  @NotBlank(message = "The field <id> is required")
  private String id;

}

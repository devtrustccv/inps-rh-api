package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovoPedidoDeclaracaoCommand implements Command {


  private PedidoDeclaracaoDTO pedidodeclaracao;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}

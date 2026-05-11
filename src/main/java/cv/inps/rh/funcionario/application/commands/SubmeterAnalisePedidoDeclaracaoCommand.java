package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoAnaliseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmeterAnalisePedidoDeclaracaoCommand implements Command {


  private PedidoDeclaracaoAnaliseDTO pedidodeclaracaoanalise;
  @NotBlank(message = "The field <id> is required")
  private String id;

}

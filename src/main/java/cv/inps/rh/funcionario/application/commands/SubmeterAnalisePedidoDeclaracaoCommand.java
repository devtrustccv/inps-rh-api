package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoAnaliseDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmeterAnalisePedidoDeclaracaoCommand implements Command {

  
  private PedidoDeclaracaoAnaliseDTO pedidodeclaracaoanalise;
  @NotBlank(message = "The field <id> is required")
  private String id;

}
package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.emprestimo.application.dto.PedidoEmprestimoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveEmprestimoCommand implements Command {

  
  private PedidoEmprestimoRequestDTO pedidoemprestimorequest;
  @NotBlank(message = "The field <emprestimoId> is required")
  private String emprestimoId;

}